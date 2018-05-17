package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.{ExchangeCurrencyFutureDataHandler, FixedPriceAlertFutureDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.collectors.TickerCollector
import com.alexitc.coinalerts.tasks.models.{FixedPriceAlertEvent, Ticker}
import com.alexitc.playsonify.core.FutureOr.Implicits.FutureOps
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class FixedPriceAlertCollector @Inject()(
    fixedPriceAlertDataHandler: FixedPriceAlertFutureDataHandler,
    exchangeCurrencyFutureDataHandler: ExchangeCurrencyFutureDataHandler)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def collect(tickerCollector: TickerCollector): Future[List[FixedPriceAlertEvent]] = {
    tickerCollector.getTickerList.flatMap { tickerList =>
      logger.info(s"Collecting ${tickerCollector.exchange} alerts")

      val alertListFuture = Future
          .sequence {
            tickerList.map { ticker =>
              val currencyName = ticker.book.currencyName.getOrElse(CurrencyName.apply(""))
              val result = for {
                // TODO: the data handler now returns the currency, we could omit this call
                currencyMaybe <- exchangeCurrencyFutureDataHandler
                    .getBy(tickerCollector.exchange, ticker.book.market, ticker.book.currency, currencyName)
                    .toFutureOr
              } yield {
                val futureListMaybe = for (currency <- currencyMaybe)
                  yield getEventsForTicker(currency, ticker)

                futureListMaybe.getOrElse {
                  Future.successful(List.empty)
                }
              }

              result.toFuture.flatMap {
                _.getOrElse(Future.successful(List.empty))
              }
            }
          }
          .map(_.flatten)

      alertListFuture.foreach { alertList =>
        logger.info(s"There are [${alertList.length}] alerts for ${tickerCollector.exchange}")
      }

      alertListFuture
    }
  }

  private def getEventsForTicker(currency: ExchangeCurrency, ticker: Ticker): Future[List[FixedPriceAlertEvent]] = {
    val currentPrice = ticker.currentPrice

    fixedPriceAlertDataHandler
        .findPendingAlertsForPrice(currency.id, currentPrice)
        .map {
          case Good(alertList) =>
            alertList.map { alert =>
              FixedPriceAlertEvent(alert, currentPrice)
            }

          case Bad(errors) =>
            logger.error(s"Cannot retrieve pending alerts for $currency, currentPrice = [$currentPrice], errors = [$errors]")
            List.empty
        }
  }
}
