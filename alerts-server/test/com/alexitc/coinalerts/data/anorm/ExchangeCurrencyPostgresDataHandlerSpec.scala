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
      val market = Market.BTC
      val currency = Currency.from("XRP").get
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)
      val result = exchangeCurrencyDataHandler.create(createModel).get

      result.exchange mustEqual exchange
      result.market mustEqual market
      result.currency mustEqual currency
    }

    "be able to create a currency having 1 character" in {
      val exchange = Exchange.BITTREX
      val market = Market.BTC
      val currency = Currency.from("R").get // Revain
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)
      val result = exchangeCurrencyDataHandler.create(createModel).get

      result.exchange mustEqual exchange
      result.market mustEqual market
      result.currency mustEqual currency
    }

    "be able to create the same currency with different name" in {
      val exchange = Exchange.BITTREX
      val market = Market.BTC
      val currency = Currency.from("BTG").get
      val currencyName1 = Some(CurrencyName("Bitcoin Gold"))
      val currencyName2 = Some(CurrencyName("Bitgem"))
      val createModel1 = CreateExchangeCurrencyModel(exchange, market, currency, currencyName1)
      val createModel2 = CreateExchangeCurrencyModel(exchange, market, currency, currencyName2)

      exchangeCurrencyDataHandler.create(createModel1).get
      val result = exchangeCurrencyDataHandler.create(createModel2).get

      result.exchange mustEqual exchange
      result.market mustEqual market
      result.currency mustEqual currency
      result.currencyName mustEqual currencyName2
    }

    "the currencyName accepts symbols from coinmarketcap" in {
      val exchange = Exchange.BITTREX
      val market = Market.BTC
      val currency = Currency.from("BTG").get
      val currencyName = Some(CurrencyName(".+/-'() []"))
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, currencyName)

      val result = exchangeCurrencyDataHandler.create(createModel)
      result.isGood mustEqual true
      result.get.currencyName mustEqual currencyName
    }

    "fail to create a repeated currency" in {
      val exchange = Exchange.BITTREX
      val market = Market.BTC
      val currency = Currency.from("LTC").get
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)

      exchangeCurrencyDataHandler.create(createModel).get
      val result = exchangeCurrencyDataHandler.create(createModel)

      result mustEqual Bad(RepeatedExchangeCurrencyError).accumulating
    }
  }

  "Retrieving a currency by id" should {
    "return the currency" in {
      val exchange = Exchange.BITTREX
      val market = Market.BTC
      val currency = Currency.from("MXN").get
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)

      val exchangeCurrency = exchangeCurrencyDataHandler.create(createModel).get
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
      val market = Market.BTC
      val currency = Currency.from("ADA").get
      val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)

      val exchangeCurrency = exchangeCurrencyDataHandler.create(createModel).get
      val result = exchangeCurrencyDataHandler.getBy(exchange, market, currency).get

      result mustEqual Some(exchangeCurrency)
    }

    "return None when the currency doesn't exist" in {
      val exchange = Exchange.BITSO
      val market = Market.BTC
      val currency = Currency.from("USD").get

      val result = exchangeCurrencyDataHandler.getBy(exchange, market, currency).get
      result mustEqual Option.empty[ExchangeCurrency]
    }
  }

  "Retrieving the currencies by exchange and market" should {
    "return the currencies" in {
      val exchange = Exchange.BITTREX
      val market = Market.USD
      val currencies = "BTC ETH ADA XRP".split(" ").flatMap(Currency.from)
      currencies.foreach { currency =>
        val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)
        exchangeCurrencyDataHandler.create(createModel)
      }

      val result = exchangeCurrencyDataHandler.getBy(exchange, market).get
      result.length mustEqual currencies.length
    }
  }

  "Retrieving the markets by exchange" should {
    "retrieve unique the markets" in {
      val exchange = Exchange.BITTREX
      val market = Market.USD
      val currencies = "BTC ETH ADA XRP".split(" ").flatMap(Currency.from)
      currencies.foreach { currency =>
        val createModel = CreateExchangeCurrencyModel(exchange, market, currency, None)
        exchangeCurrencyDataHandler.create(createModel)
      }

      val result = exchangeCurrencyDataHandler.getMarkets(exchange).get
      val expected = result.distinct
      result.length mustEqual expected.length
    }
  }
}
