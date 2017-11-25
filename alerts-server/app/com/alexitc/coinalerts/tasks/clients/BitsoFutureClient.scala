package com.alexitc.coinalerts.tasks.clients

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.models.Book
import com.alexitc.coinalerts.tasks.Ticker
import com.bitso.{Bitso, BitsoTicker}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class BitsoFutureClient @Inject() (bitso: Bitso)(implicit ec: TaskExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def getTickerList: Future[List[Ticker]] = Future {
    bitso.getTicker
        .toList
        .flatMap(createTicker)
  }

  private def createTicker(bitsoTicker: BitsoTicker): Option[Ticker] = {
    Book.fromString(bitsoTicker.getBook)
        .map { book =>
          Ticker(book, bitsoTicker.getLast)
        }
        .orElse {
          logger.warn(s"Unable to create book from string = [${bitsoTicker.getBook}]")
          None
        }
  }
}
