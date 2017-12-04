package com.alexitc.coinalerts.modules

import javax.inject.Singleton

import com.alexitc.coinalerts.services.external.{BitsoService, BittrexService}
import com.alexitc.coinalerts.services.validators._
import com.google.inject.{AbstractModule, Provides}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class MarketBookValidatorModule extends AbstractModule {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def configure(): Unit = {
    bind(classOf[MarketBookValidator]).to(classOf[MarketBookValidatorImpl])
  }

  @Provides
  @Singleton
  def bitsoBookValidator(bitsoService: BitsoService): BitsoBookValidator = {
    val future = bitsoService.availableBooks
    val books = Await.result(future, 15.seconds)

    if (books.isEmpty) {
      // TODO: use default state, maybe loaded from file?
      logger.error("There are no available books from BITSO, application is shutting down")
      sys.exit(0)
    }

    new BitsoInMemoryBookValidator(books)
  }

  @Provides
  @Singleton
  def bittrexBookValidator(bittrexService: BittrexService): BittrexBookValidator = {
    val future = bittrexService.availableBooks
    val books = Await.result(future, 15.seconds)

    if (books.isEmpty) {
      // TODO: use default state, maybe loaded from file?
      logger.error("There are no available books from BITTREX, application is shutting down")
      sys.exit(0)
    }

    new BittrexInMemoryBookValidator(books)
  }
}
