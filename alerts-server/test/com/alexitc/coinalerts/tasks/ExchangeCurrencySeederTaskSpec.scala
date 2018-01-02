package com.alexitc.coinalerts.tasks

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.TaskExecutionContext
import com.alexitc.coinalerts.data.ExchangeCurrencyInMemoryDataHandler
import com.alexitc.coinalerts.models.{Book, Currency, Exchange, Market}
import com.alexitc.coinalerts.services.external.{BitsoService, BittrexService}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Future

class ExchangeCurrencySeederTaskSpec extends WordSpec with MustMatchers with ScalaFutures {

  private val actorSystem = ActorSystem(this.getClass.getSimpleName)
  private implicit val taskEC = new TaskExecutionContext(actorSystem)

  "Currency seeder task" should {
    "be able to seed new currencies" in {
      val bitsoBooks = "BTC_LTC BTC_ETH".split(" ").flatMap(Book.fromString)
      val bittrexBooks = "ETH_XRP ETH_ADA ETH_XMR".split(" ").flatMap(Book.fromString)
      val currencyDataHandler = new ExchangeCurrencyInMemoryDataHandler {}

      val task = new ExchangeCurrencySeederTask(
        bitsoService(bitsoBooks),
        bittrexService(bittrexBooks),
        currencyDataHandler)

      whenReady(task.execute()) { _ =>
        bitsoBooks.foreach { book =>
          currencyDataHandler.getBy(Exchange.BITSO, book.market, book.currency).get.isDefined mustEqual true
        }
        bittrexBooks.foreach { book =>
          currencyDataHandler.getBy(Exchange.BITTREX, book.market, book.currency).get.isDefined mustEqual true
        }
      }
    }

    "ignored existing currencies" in {
      val bitsoBooks = "BTC_LTC".split(" ").flatMap(Book.fromString)
      val currencyDataHandler = new ExchangeCurrencyInMemoryDataHandler {}
      currencyDataHandler.create(Exchange.BITSO, Market("BTC"), Currency("LTC"))

      val task = new ExchangeCurrencySeederTask(
        bitsoService(bitsoBooks),
        bittrexService(List.empty),
        currencyDataHandler)

      whenReady(task.execute()) { _ =>
        bitsoBooks.foreach { book =>
          currencyDataHandler.getBy(Exchange.BITSO, book.market, book.currency).get.isDefined mustEqual true
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
}
