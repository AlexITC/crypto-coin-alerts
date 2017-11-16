package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.AlertFutureDataHandler
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}

import scala.concurrent.Future

class BitsoAlertCollector @Inject() (
    bitsoClient: BitsoFutureClient,
    protected val alertDataHandler: AlertFutureDataHandler)(
    implicit ec: TaskExecutionContext)
    extends AlertCollector {

  override def collect(): Future[List[AlertEvent]] = {
    bitsoClient.getTickerList.flatMap { tickerList =>
      logger.info("Looking for BITSO alerts")

      val alertListFuture = Future
          .sequence { tickerList.map(getEventsForTicker) }
          .map(_.flatten)

      alertListFuture.foreach { alertList =>
        logger.info(s"There are [${alertList.length}] alerts for BITSO")
      }

      alertListFuture
    }
  }

  private def getEventsForTicker(ticker: Ticker): Future[List[AlertEvent]] = {
    val book = ticker.book
    val currentPrice = ticker.currentPrice

    logger.info(s"Looking for alerts on BITSO for [${book.string}] for price = [$currentPrice]")
    alertDataHandler
        .findPendingAlertsForPrice(Market.BITSO, book, currentPrice)
        .flatMap {
          case Good(alertList) =>
            val futures = alertList.map { alert => createEvent(alert, currentPrice) }
            Future.sequence(futures)

          case Bad(errors) =>
            logger.error(s"Cannot retrieve pending alerts for BITSO, book = [${book.string}], currentPrice = [$currentPrice], errors = [$errors]")
            Future.successful(List.empty)
        }
  }
}
