package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureOr.Implicits.FutureOps
import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.ExchangeCurrencyBlockingDataHandler
import com.alexitc.coinalerts.data.async.{NewCurrencyAlertFutureDataHandler, UserFutureDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.external.{BitsoService, BittrexService, KucoinService}
import com.alexitc.coinalerts.services.{EmailMessagesProvider, EmailServiceTrait}
import org.scalactic.TypeCheckedTripleEquals._
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory
import play.api.i18n.Lang

import scala.concurrent.Future
import scala.util.control.NonFatal

class ExchangeCurrencySeederTask @Inject() (
    bitsoService: BitsoService,
    bittrexService: BittrexService,
    kucoinService: KucoinService,
    exchangeCurrencyBlockingDataHandler: ExchangeCurrencyBlockingDataHandler,
    newCurrencyAlertFutureDataHandler: NewCurrencyAlertFutureDataHandler,
    userFutureDataHandler: UserFutureDataHandler,
    emailServiceTrait: EmailServiceTrait,
    emailMessagesProvider: EmailMessagesProvider)(
    implicit val ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def execute(): Future[Unit] = {
    logger.info("Looking for new currencies")

    val result = exchangeCurrencyBlockingDataHandler.getAll() match {
      case Good(currencies) =>
        execute(currencies).map(_ => ()).recover {
          case NonFatal(ex) => logger.error("error while looking for new currencies", ex)
        }

      case Bad(errors) =>
        logger.error(s"unexpected error while retrieving currency list, errors = $errors")
        Future.unit
    }

    result.foreach { _ =>
      logger.info("Task completed")
    }

    result
  }

  private def execute(currencies: List[ExchangeCurrency]) = {
    val bitsoResult = bitsoService.availableBooks().map { books =>
      seed(currencies, Exchange.BITSO, books)
    }

    val bittrexResult = bittrexService.availableBooks().map { books =>
      seed(currencies, Exchange.BITTREX, books)
    }

    val kucoinResult = kucoinService.availableBooks().map { books =>
      seed(currencies, Exchange.KUCOIN, books)
    }

    val results = List(bitsoResult, bittrexResult, kucoinResult)
    Future.sequence(results).map(_.flatten)
  }

  private def seed(currencies: List[ExchangeCurrency], exchange: Exchange, books: List[Book]) = {
    val newBooks = books.filterNot { book =>
      currencies.exists { currency =>
        exchange === currency.exchange &&
          book.market === currency.market &&
          book.currency === currency.currency
      }
    }

    logger.info(s"there are [${newBooks.length}] currencies for [$exchange]")
    if (newBooks.nonEmpty && (books.length !== newBooks.length)) {
      logger.info(s"new currencies found for [$exchange]: $newBooks")
      triggerAlerts(newBooks, exchange)
    }

    for (book <- newBooks)
      yield exchangeCurrencyBlockingDataHandler.create(exchange, book.market, book.currency) match {
        case Good(_) => ()
        case Bad(errors) =>
          logger.error(s"error while creating book = [${book.string}] for exchange = [$exchange], errors = $errors")
      }
  }

  private def triggerAlerts(books: List[Book], exchange: Exchange) = {
    newCurrencyAlertFutureDataHandler.getBy(exchange).flatMap {
      case Good(alerts) =>
        val futures = alerts.map { alert =>
          sendAlert(alert, books)
        }

        Future.sequence(futures)

      case Bad(errors) =>
        logger.error(s"Failed to retrieve alerts by $exchange, errors = $errors")
        Future.unit
    }.recover {
      case NonFatal(ex) =>
        logger.error("Failed to send alerts", ex)
    }
  }

  private def sendAlert(alert: NewCurrencyAlert, books: List[Book]): Future[Unit] = {
    val result = for {
      user <- userFutureDataHandler.getVerifiedUserById(alert.userId).toFutureOr
      preferences <- userFutureDataHandler.getUserPreferences(alert.userId).toFutureOr
    } yield {
      implicit val lang: Lang = preferences.lang
      val subject = emailMessagesProvider.newCurrenciesAlertSubject(alert.exchange)
      val text = emailMessagesProvider.newCurrenciesAlertText(books)
      emailServiceTrait.sendEmail(user.email, subject, text)
    }

    result.toFuture.map {
      case Good(_) => ()
      case Bad(errors) =>
        logger.error(s"Failed to send alerts to user = ${alert.userId}, exchange = ${alert.exchange}, books = $books, errors = $errors")
    }.recover {
      case NonFatal(ex) =>
        logger.error(s"Failed to send alerts to user = ${alert.userId}, exchange = ${alert.exchange}, books = $books", ex)
    }
  }
}
