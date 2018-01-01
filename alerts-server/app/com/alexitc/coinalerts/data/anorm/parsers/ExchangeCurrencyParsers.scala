package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser.{int, str}
import anorm.~
import com.alexitc.coinalerts.models._

object ExchangeCurrencyParsers {

  val parseCurrencyId = int("currency_id").map(ExchangeCurrencyId.apply)
  val parseExchange = str("exchange").map(Exchange.fromDatabaseString)
  val parseMarket = str("market").map(Market.apply)
  val parseCurrency = str("currency").map(Currency.apply)

  val parseExchangeCurrency = (parseCurrencyId ~ parseExchange ~ parseMarket ~ parseCurrency).map {
    case id ~ exchange ~ market ~ currency => ExchangeCurrency(id, exchange, market, currency)
  }
}
