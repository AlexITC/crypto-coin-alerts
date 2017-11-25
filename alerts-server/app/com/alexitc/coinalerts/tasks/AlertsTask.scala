package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.FutureOps
import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.async.{AlertFutureDataHandler, UserFutureDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.{EmailMessagesProvider, EmailServiceTrait, EmailText}
import com.alexitc.coinalerts.tasks.collectors.{BitsoTickerCollector, BittrexTickerCollector}
import com.alexitc.coinalerts.tasks.models.AlertEvent
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory
import play.api.i18n.{Lang, MessagesApi}

import scala.concurrent.Future
import scala.util.control.NonFatal

class AlertsTask @Inject() (
    alertCollector: AlertCollector,
    bitsoTickerCollector: BitsoTickerCollector,
    bittrexAlertCollector: BittrexTickerCollector,
    userDataHandler: UserFutureDataHandler,
    alertDataHandler: AlertFutureDataHandler,
    emailMessagesProvider: EmailMessagesProvider,
    messagesApi: MessagesApi,
    emailServiceTrait: EmailServiceTrait)(
    implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val tickerCollectorList = List(bitsoTickerCollector, bittrexAlertCollector)

  def execute(): Future[Unit] = {
    val futures = tickerCollectorList.map { tickerCollector =>
      alertCollector.collect(tickerCollector)
    }

    Future.sequence(futures)
        .map(_.flatten)
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
    val result = for {
      user <- userDataHandler.getVerifiedUserById(userId).toFutureOr
      preferences <- userDataHandler.getUserPreferences(userId).toFutureOr
      _ <- {
        val emailSubject = emailMessagesProvider.yourAlertsSubject(preferences.lang)
        val emailText = createEmailText(eventList)(preferences.lang)
        emailServiceTrait.sendEmail(user.email, emailSubject, emailText).toFutureOr
      }
    } yield eventList.foreach { event =>
      alertDataHandler.markAsTriggered(event.alert.id)
    }

    result.toFuture.map {
      case Good(_) => ()
      case Bad(errors) =>
        logger.error(s"Error while trying to send alerts by email to user = [${userId.string}], errors = [$errors]")

    }.recover {
      case NonFatal(ex) =>
        logger.error(s"Error while trying to send alerts by email to user = [${userId.string}]", ex)
    }
  }

  private def groupByMarket(eventList: List[AlertEvent]): Map[Market, List[AlertEvent]] = {
    eventList.groupBy(_.alert.market)
  }

  private def createEmailText(eventList: List[AlertEvent])(implicit lang: Lang): EmailText = {
    val text = groupByMarket(eventList)
        .map {
          case (market, marketEvents) =>
            val marketLines = marketEvents.map(createText).mkString("\n")
            s"${market.string}:\n$marketLines"
        }
        .mkString("\n\n\n")

    new EmailText(text)
  }

  private def createText(event: AlertEvent)(implicit lang: Lang): String = {
    val alert = event.alert
    // TODO: Compute % difference for base_price alerts
    if (alert.isGreaterThan) {
      messagesApi("message.alert.priceIncreased", alert.book.string, event.currentPrice)
    } else {
      messagesApi("message.alert.priceDecreased", alert.book.string, event.currentPrice)
    }
  }
}
