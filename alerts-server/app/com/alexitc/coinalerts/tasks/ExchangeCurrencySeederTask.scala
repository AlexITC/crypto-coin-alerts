package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.ExchangeCurrencyBlockingDataHandler
import com.alexitc.coinalerts.models.{Book, Exchange, ExchangeCurrency}
import com.alexitc.coinalerts.services.external.{BitsoService, BittrexService}
import org.scalactic.TypeCheckedTripleEquals._
import org.scalactic.{Bad, Good}
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.control.NonFatal

class ExchangeCurrencySeederTask @Inject() (
    bitsoService: BitsoService,
    bittrexService: BittrexService,
    exchangeCurrencyBlockingDataHandler: ExchangeCurrencyBlockingDataHandler)(
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

    result
  }

  private def execute(currencies: List[ExchangeCurrency]) = {
    val bitsoResult = bitsoService.availableBooks.map { books =>
      seed(currencies, Exchange.BITSO, books)
    }

    val bittrexResult = bittrexService.availableBooks.map { books =>
      seed(currencies, Exchange.BITTREX, books)
    }

    val results = List(bitsoResult, bittrexResult)
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
    if (books.length !== newBooks.length) {
      logger.info(s"new currencies found for [$exchange]: $newBooks")
    }

    for (book <- newBooks)
      yield exchangeCurrencyBlockingDataHandler.create(exchange, book.market, book.currency) match {
        case Good(_) => ()
        case Bad(errors) =>
          logger.error(s"error while creating book = [${book.string}] for exchange = [$exchange], errors = $errors")
      }
  }
}
