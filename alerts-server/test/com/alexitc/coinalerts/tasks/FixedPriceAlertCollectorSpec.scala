package com.alexitc.coinalerts.tasks

import com.alexitc.coinalerts.commons.DataHelper
import com.alexitc.coinalerts.commons.ExecutionContexts._
import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.data.async.{ExchangeCurrencyFutureDataHandler, FixedPriceAlertFutureDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.collectors.TickerCollector
import com.alexitc.coinalerts.tasks.models.Ticker
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Future

class FixedPriceAlertCollectorSpec extends WordSpec with MustMatchers with ScalaFutures {

  implicit lazy val userDataHandler = new UserInMemoryDataHandler {}

  "Collecting alerts" should {
    "collect all alerts matching the price criteria" in {
      val exchangeCurrencyDataHandler = new ExchangeCurrencyInMemoryDataHandler {}

      val fixedPriceAlertBlockingDataHandler = new FixedPriceAlertInMemoryDataHandler {}

      val alertCollector = createAlertCollector(exchangeCurrencyDataHandler, fixedPriceAlertBlockingDataHandler)

      // create currencies
      val exchange = Exchange.BITSO
      val market = Market("BTC")
      val BTC_MXN = exchangeCurrencyDataHandler.create(exchange, market, Currency("MXN")).get
      val BTC_ADA = exchangeCurrencyDataHandler.create(exchange, market, Currency("ADA")).get

      // create alerts
      val trigger1 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_MXN.id, true, BigDecimal("79999"), None),
        DataHelper.createVerifiedUser().id).get

      val trigger2 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_MXN.id, false, BigDecimal("80001"), None),
        DataHelper.createVerifiedUser().id).get

      val nonTrigger1 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_MXN.id, false, BigDecimal("79999"), None),
        DataHelper.createVerifiedUser().id).get

      val nonTrigger2 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_ADA.id, true, BigDecimal("1001"), None),
        DataHelper.createVerifiedUser().id).get

      val trigger3 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_ADA.id, false, BigDecimal("1000"), None),
        DataHelper.createVerifiedUser().id).get

      val trigger4 = fixedPriceAlertBlockingDataHandler.create(
        CreateFixedPriceAlertModel(BTC_ADA.id, true, BigDecimal("1000"), None),
        DataHelper.createVerifiedUser().id).get

      // group them
      val triggered = List(trigger1, trigger2, trigger3, trigger4)
      val nonTriggered = List(nonTrigger1, nonTrigger2)

      // the ticker collector
      val tickerList = List(
        Ticker(Book(BTC_MXN.market, BTC_MXN.currency), BigDecimal("80000")),
        Ticker(Book(BTC_ADA.market, BTC_ADA.currency), BigDecimal("1000")))
      val tickerCollector = createTicketCollector(exchange, tickerList)

      whenReady(alertCollector.collect(tickerCollector)) { events =>
        events.length mustEqual 4

        triggered.foreach { alert =>
          val result = events.exists { event =>
            event.alert.id == alert.id &&
            event.exchangeCurrency.id == alert.exchangeCurrencyId
          }

          result mustEqual true
        }
      }
    }
  }

  private def createTicketCollector(givenExchange: Exchange, tickerList: List[Ticker]) = {
    new TickerCollector {
      override def getTickerList: Future[List[Ticker]] = {
        Future.successful(tickerList)
      }

      override def exchange: Exchange = givenExchange
    }
  }

  private def createAlertCollector(
      exchangeCurrencyDataHandler: ExchangeCurrencyBlockingDataHandler,
      fixedPriceAlertBlockingDataHandler: FixedPriceAlertBlockingDataHandler) = {

    val exchangeCurrencyFutureDataHandler = new ExchangeCurrencyFutureDataHandler(
      exchangeCurrencyDataHandler)

    val fixedPriceAlertFutureDataHandler = new FixedPriceAlertFutureDataHandler(
      fixedPriceAlertBlockingDataHandler)

    new FixedPriceAlertCollector(
      fixedPriceAlertFutureDataHandler,
      exchangeCurrencyFutureDataHandler)
  }
}

