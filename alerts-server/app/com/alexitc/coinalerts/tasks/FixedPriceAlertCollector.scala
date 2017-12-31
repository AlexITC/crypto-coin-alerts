package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.collectors.TickerCollector
import com.alexitc.coinalerts.tasks.models.{FixedPriceAlertEvent, Ticker}
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class FixedPriceAlertCollector @Inject()(
    fixedPriceAlertDataHandler: FixedPriceAlertFutureDataHandler)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private def getCurrency(exchange: Exchange, book: Book): ExchangeCurrency = {
    ??? // TODO: Implement
  }

  def collect(tickerCollector: TickerCollector): Future[List[FixedPriceAlertEvent]] = {
    tickerCollector.getTickerList.flatMap { tickerList =>
      logger.info(s"Collecting ${tickerCollector.exchange} alerts")

      val alertListFuture = Future
          .sequence {
            tickerList.map { ticker =>
              val currency = getCurrency(tickerCollector.exchange, ticker.book)
              getEventsForTicker(currency, ticker)
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
        .flatMap {
          case Good(alertList) =>
            val futures = alertList.map { alert => createEvent(alert, currency, currentPrice) }
            Future.sequence(futures)

          case Bad(errors) =>
            logger.error(s"Cannot retrieve pending alerts for $currency, currentPrice = [$currentPrice], errors = [$errors]")
            Future.successful(List.empty)
        }
  }

  private def createEvent(
      alert: FixedPriceAlert,
      currency: ExchangeCurrency,
      currentPrice: BigDecimal)(
      implicit ec: ExecutionContext): Future[FixedPriceAlertEvent] = {

    val event = FixedPriceAlertEvent(alert, currency, currentPrice)
    Future.successful(event)
  }
}
