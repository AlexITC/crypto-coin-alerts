package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.{AlertFutureDataHandler, UserAsyncDAL}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.EmailServiceTrait
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class AlertsTask @Inject() (
    alertCollector: AlertCollector,
    bitsoTickerCollector: BitsoTickerCollector,
    userAsyncDAL: UserAsyncDAL,
    alertDataHandler: AlertFutureDataHandler,
    emailServiceTrait: EmailServiceTrait)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def execute(): Future[Unit] = {
    alertCollector.collect(bitsoTickerCollector)
        .map(groupByUser)
        .flatMap { userAlerts =>
          userAlerts.foreach {
            case (userId, eventList) => triggerAlerts(userId, eventList)
          }

          Future.unit
        }
  }

  private def groupByUser(eventList: List[AlertEvent]): Map[UserId, List[AlertEvent]] = {
    eventList.groupBy(_.alert.userId)
  }

  private def triggerAlerts(userId: UserId, eventList: List[AlertEvent]): Future[Unit] = {
    val text = groupByMarket(eventList)
        .map {
          case (market, marketEvents) =>
            val marketLines = marketEvents.map(createText).mkString("\n")
            s"${market.string}:\n$marketLines"
        }
        .mkString("\n\n\n")

    userAsyncDAL.getVerifiedUserById(userId).flatMap {
      case Good(user) =>
        // TODO: i18n
        val header = "Your Crypto Coin Alerts"
        emailServiceTrait.sendEmail(user.email, header, text).flatMap {
          case Bad(errors) =>
            logger.error(s"Error while sending alerts by email to user = [${userId.string}], errors = [$errors]")
            Future.unit

          case _ =>
            eventList.foreach { event =>
              alertDataHandler.markAsTriggered(event.alert.id)
            }

            Future.unit
        }
      case Bad(errors) =>
        logger.error(s"Error while retrieving user = [${userId.string}] for sending alerts, errors = [$errors]")
        Future.unit
    }
  }

  private def groupByMarket(eventList: List[AlertEvent]): Map[Market, List[AlertEvent]] = {
    eventList.groupBy(_.alert.market)
  }

  // TODO: i18n
  private def createText(event: AlertEvent): String = {
    val alert = event.alert
    val action = if (alert.isGreaterThan) "increased" else "decreased"
    // TODO: Compute % difference for base_price alerts
    s"${alert.book.string} has $action to ${event.currentPrice}"
  }
}
