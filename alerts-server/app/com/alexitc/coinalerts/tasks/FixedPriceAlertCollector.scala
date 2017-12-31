package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.models.{FixedPriceAlert, Exchange}
import com.alexitc.coinalerts.tasks.collectors.TickerCollector
import com.alexitc.coinalerts.tasks.models.{FixedPriceAlertEvent, Ticker}
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class FixedPriceAlertCollector @Inject()(
    fixedPriceAlertDataHandler: FixedPriceAlertFutureDataHandler)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def collect(tickerCollector: TickerCollector): Future[List[FixedPriceAlertEvent]] = {
    tickerCollector.getTickerList.flatMap { tickerList =>
      logger.info(s"Collecting ${tickerCollector.market} alerts")

      val alertListFuture = Future
          .sequence {
            tickerList.map { ticker =>
              getEventsForTicker(tickerCollector.market, ticker)
            }
          }
          .map(_.flatten)

      alertListFuture.foreach { alertList =>
        logger.info(s"There are [${alertList.length}] alerts for ${tickerCollector.market}")
      }

      alertListFuture
    }
  }

  private def getEventsForTicker(market: Exchange, ticker: Ticker): Future[List[FixedPriceAlertEvent]] = {
    val book = ticker.book
    val currentPrice = ticker.currentPrice

    logger.info(s"Collecting alerts on $market for [${book.string}] and price = [$currentPrice]")
    fixedPriceAlertDataHandler
        .findPendingAlertsForPrice(market, book, currentPrice)
        .flatMap {
          case Good(alertList) =>
            val futures = alertList.map { alert => createEvent(alert, currentPrice) }
            Future.sequence(futures)

          case Bad(errors) =>
            logger.error(s"Cannot retrieve pending alerts for $market, book = [${book.string}], currentPrice = [$currentPrice], errors = [$errors]")
            Future.successful(List.empty)
        }
  }

  private def createEvent(
      alert: FixedPriceAlert,
      currentPrice: BigDecimal)(
      implicit ec: ExecutionContext): Future[FixedPriceAlertEvent] = {

    val event = FixedPriceAlertEvent(alert, currentPrice)
    Future.successful(event)
  }
}
