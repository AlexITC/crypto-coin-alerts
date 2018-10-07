package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.data.ExchangeCurrencyBlockingDataHandler
import com.alexitc.coinalerts.models.{Book, CreateExchangeCurrencyModel, Exchange}

object CurrencySeeder {

  private val bitsoBookList = "BTC_MXN ETH_MXN XRP_MXN LTC_MXN".split(" ").flatMap(Book.fromString)
  private val bittrexBookList = "BTC_XRP BTC_ETH BTC_ADA ETH_XRP ETH_ADA ETH_XMR".split(" ").flatMap(Book.fromString)

  def seed(implicit exchangeCurrencyDataHandler: ExchangeCurrencyBlockingDataHandler) = {
    seedExchangeCurrencyList(Exchange.BITSO, bitsoBookList)
    seedExchangeCurrencyList(Exchange.BITTREX, bittrexBookList)
  }

  private def seedExchangeCurrencyList(exchange: Exchange, books: Seq[Book])(
      implicit exchangeCurrencyDataHandler: ExchangeCurrencyBlockingDataHandler) = {
    books.map { book =>
      val createModel = CreateExchangeCurrencyModel(exchange, book.market, book.currency, None)
      exchangeCurrencyDataHandler.create(createModel)
    }
  }
}
