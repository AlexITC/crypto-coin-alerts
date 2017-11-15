package com.alexitc.coinalerts.tasks

import com.alexitc.coinalerts.data.async.AlertFutureDataHandler
import com.alexitc.coinalerts.models.{Alert, AlertType}
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

trait AlertCollector {

  def collect(): Future[List[AlertEvent]]

  protected val logger = LoggerFactory.getLogger(this.getClass)

  protected def alertDataHandler: AlertFutureDataHandler

  protected def createEvent(
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
