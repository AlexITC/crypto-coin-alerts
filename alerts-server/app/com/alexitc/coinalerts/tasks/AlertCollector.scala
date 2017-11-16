package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.AlertFutureDataHandler
import com.alexitc.coinalerts.models.{Alert, AlertType, Market}
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class AlertCollector @Inject()(
    alertDataHandler: AlertFutureDataHandler)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def collect(tickerCollector: TickerCollector): Future[List[AlertEvent]] = {
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

  private def getEventsForTicker(market: Market, ticker: Ticker): Future[List[AlertEvent]] = {
    val book = ticker.book
    val currentPrice = ticker.currentPrice

    logger.info(s"Collecting alerts on $market for [${book.string}] and price = [$currentPrice]")
    alertDataHandler
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
      alert: Alert,
      currentPrice: BigDecimal)(
      implicit ec: ExecutionContext): Future[AlertEvent] = alert.alertType match {

    case AlertType.BASE_PRICE =>
      alertDataHandler.findBasePriceAlert(alert.id).map {
        case Good(basePriceAlert) =>
          AlertEvent(alert, currentPrice, Option(basePriceAlert.basePrice))

        case Bad(errors) =>
          logger.warn(s"Got errors retrieving BASE_PRICE alert = [${alert.id}], errors = [$errors]")
          AlertEvent(alert, currentPrice, None)
      }

    case AlertType.DEFAULT =>
      val event = AlertEvent(alert, currentPrice, None)
      Future.successful(event)

    case AlertType.UNKNOWN(string) =>
      logger.warn(s"Got UNKNOWN alert type = [$string] for id [${alert.id}]")

      val event = AlertEvent(alert, currentPrice, None)
      Future.successful(event)
  }
}
