package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.commons.PostgresDataHandlerSpec
import com.alexitc.coinalerts.errors.RepeatedExchangeCurrencyError
import com.alexitc.coinalerts.models._
import org.scalactic.Bad

class ExchangeCurrencyPostgresDataHandlerSpec extends PostgresDataHandlerSpec {

  override def seedCurrencies: Boolean = false

  "Creating a currency" should {
    "succeed with a new currency" in {
      val exchange = Exchange.BITTREX
      val market = Market("BTC")
      val currency = Currency("XRP")
      val result = exchangeCurrencyDataHandler.create(exchange, market, currency).get

      result.exchange mustEqual exchange
      result.market mustEqual market
      result.currency mustEqual currency
    }

    "be able to create a currency having 2 characters" in {
      val exchange = Exchange.BITTREX
      val market = Market("BTC")
      val currency = Currency("TX") // TransferCoin
      val result = exchangeCurrencyDataHandler.create(exchange, market, currency).get

      result.exchange mustEqual exchange
      result.market mustEqual market
      result.currency mustEqual currency
    }

    "fail to create a repeated currency" in {
      val exchange = Exchange.BITTREX
      val market = Market("BTC")
      val currency = Currency("LTC")

      exchangeCurrencyDataHandler.create(exchange, market, currency)
      val result = exchangeCurrencyDataHandler.create(exchange, market, currency)

      result mustEqual Bad(RepeatedExchangeCurrencyError).accumulating
    }
  }

  "Retrieving a currency by id" should {
    "return the currency" in {
      val exchange = Exchange.BITTREX
      val market = Market("BTC")
      val currency = Currency("MXN")

      val exchangeCurrency = exchangeCurrencyDataHandler.create(exchange, market, currency).get
      val result = exchangeCurrencyDataHandler.getBy(exchangeCurrency.id).get

      result mustEqual Some(exchangeCurrency)
    }

    "return None when the currency id is not available" in {
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val unknownId = ExchangeCurrencyId(currencies.map(_.id.int).max + 1)
      val result = exchangeCurrencyDataHandler.getBy(unknownId).get

      result mustEqual Option.empty[ExchangeCurrency]
    }
  }

  "Retrieving a currency by exchange, market and currency" should {
    "return the currency" in {
      val exchange = Exchange.BITTREX
      val market = Market("BTC")
      val currency = Currency("ADA")

      val exchangeCurrency = exchangeCurrencyDataHandler.create(exchange, market, currency).get
      val result = exchangeCurrencyDataHandler.getBy(exchange, market, currency).get

      result mustEqual Some(exchangeCurrency)
    }

    "return None when the currency doesn't exist" in {
      val exchange = Exchange.BITSO
      val market = Market("BTC")
      val currency = Currency("USD")

      val result = exchangeCurrencyDataHandler.getBy(exchange, market, currency).get
      result mustEqual Option.empty[ExchangeCurrency]
    }
  }

  "Retrieving the currencies by exchange and market" should {
    "return the currencies" in {
      val exchange = Exchange.BITTREX
      val market = Market("USD")
      val currencies = "BTC ETH ADA XRP".split(" ").map(Currency.apply)
      currencies.foreach { currency =>
        exchangeCurrencyDataHandler.create(exchange, market, currency)
      }

      val result = exchangeCurrencyDataHandler.getBy(exchange, market).get
      result.length mustEqual currencies.length
    }
  }

  "Retrieving the markets by exchange" should {
    "retrieve the markets" in {
      val result = exchangeCurrencyDataHandler.getMarkets(Exchange.BITSO)
      result.isGood mustEqual true
    }
  }
}
