package com.alexitc.coinalerts.tasks

import com.alexitc.coinalerts.commons.ExecutionContexts._
import com.alexitc.coinalerts.commons.FakeEmailService
import com.alexitc.coinalerts.data.async.{NewCurrencyAlertFutureDataHandler, UserFutureDataHandler}
import com.alexitc.coinalerts.data.{
  ExchangeCurrencyBlockingDataHandler,
  ExchangeCurrencyInMemoryDataHandler,
  NewCurrencyAlertInMemoryDataHandler,
  UserInMemoryDataHandler
}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.external._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Future

class ExchangeCurrencySeederTaskSpec extends WordSpec with MustMatchers with ScalaFutures {

  "Currency seeder task" should {
    "be able to seed new currencies" in {
      val bitsoBooks = "BTC_LTC BTC_ETH".split(" ").flatMap(Book.fromString)
      val bittrexBooks = "ETH_XRP ETH_ADA ETH_XMR".split(" ").flatMap(Book.fromString)
      val currencyDataHandler = new ExchangeCurrencyInMemoryDataHandler {}

      val task = seederTask(bitsoService(bitsoBooks), bittrexService(bittrexBooks), currencyDataHandler)

      whenReady(task.execute()) { _ =>
        bitsoBooks.foreach { book =>
          currencyDataHandler
            .getBy(Exchange.BITSO, book.market, book.currency, book.currencyName.getOrElse(CurrencyName("")))
            .get
            .isDefined mustEqual true
        }
        bittrexBooks.foreach { book =>
          currencyDataHandler
            .getBy(Exchange.BITTREX, book.market, book.currency, book.currencyName.getOrElse(CurrencyName("")))
            .get
            .isDefined mustEqual true
        }
      }
    }

    "ignored existing currencies" in {
      val bitsoBooks = "BTC_LTC".split(" ").flatMap(Book.fromString)
      val currencyDataHandler = new ExchangeCurrencyInMemoryDataHandler {}
      val createModel = CreateExchangeCurrencyModel(Exchange.BITSO, Market.BTC, Currency.from("LTC").get, None)
      currencyDataHandler.create(createModel)

      val task = seederTask(bitsoService(bitsoBooks), bittrexService(List.empty), currencyDataHandler)

      whenReady(task.execute()) { _ =>
        bitsoBooks.foreach { book =>
          currencyDataHandler
            .getBy(Exchange.BITSO, book.market, book.currency, book.currencyName.getOrElse(CurrencyName("")))
            .get
            .isDefined mustEqual true
        }
      }
    }
  }

  private def bitsoService(books: Seq[Book]) = new BitsoService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def bittrexService(books: Seq[Book]) = new BittrexService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def kucoinService(books: Seq[Book]) = new KucoinService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def binanceService(books: Seq[Book]) = new BinanceService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def hitbtcService(books: Seq[Book]) = new HitbtcService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def coinmarketcapService(books: Seq[Book]) = new CoinmarketcapService(null)(null) {
    override def availableBooks: Future[List[Book]] = {
      Future.successful(books.toList)
    }
  }

  private def seederTask(
      bitsoService: BitsoService,
      bittrexService: BittrexService,
      currencyDataHandler: ExchangeCurrencyBlockingDataHandler): ExchangeCurrencySeederTask = {

    val newCurrencyAlertDataHandler = new NewCurrencyAlertFutureDataHandler(new NewCurrencyAlertInMemoryDataHandler)
    val userFutureDataHandler = new UserFutureDataHandler(new UserInMemoryDataHandler {})

    new ExchangeCurrencySeederTask(
        bitsoService,
        bittrexService,
        kucoinService(List.empty),
        binanceService(List.empty),
        hitbtcService(List.empty),
        coinmarketcapService(List.empty),
        currencyDataHandler,
        newCurrencyAlertDataHandler,
        userFutureDataHandler,
        new FakeEmailService,
        null
    )
  }
}
