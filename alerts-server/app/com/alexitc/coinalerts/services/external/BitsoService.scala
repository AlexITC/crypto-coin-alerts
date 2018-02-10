package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.config.ExternalServiceExecutionContext
import com.alexitc.coinalerts.models.{BitsoBook, Book}
import com.alexitc.coinalerts.tasks.models.Ticker
import com.bitso.{Bitso, BitsoTicker}
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.control.NonFatal

class BitsoService @Inject() (
    bitso: Bitso)(
    implicit ec: ExternalServiceExecutionContext)
    extends ExchangeService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def availableBooks(): Future[List[Book]] = {
    val result = Future {
      bitso.getTicker.toList.flatMap { ticker =>
        BitsoBook.fromString(ticker.getBook)
            .orElse {
              logger.warn(s"Unable to create book from string = [${ticker.getBook}]")
              None
            }
      }
    }

    result.recover {
      case NonFatal(ex) =>
        logger.warn("Failed to retrieve available books from BITSO", ex)
        List.empty
    }
  }

  override def getTickerList(): Future[List[Ticker]] = {
    val result = Future {
      bitso.getTicker
          .toList
          .flatMap(createTicker)
    }

    result.recover {
      case NonFatal(ex) =>
        logger.warn("Failed to retrieve ticker list from BITSO", ex)
        List.empty
    }
  }

  private def createTicker(bitsoTicker: BitsoTicker): Option[Ticker] = {
    BitsoBook.fromString(bitsoTicker.getBook)
        .map { book =>
          Ticker(book, bitsoTicker.getLast)
        }
        .orElse {
          logger.warn(s"Unable to create book from string = [${bitsoTicker.getBook}]")
          None
        }
  }
}
