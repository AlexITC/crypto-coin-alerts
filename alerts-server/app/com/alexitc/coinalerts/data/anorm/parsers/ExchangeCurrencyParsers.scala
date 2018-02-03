package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser.{int, str}
import anorm.~
import com.alexitc.coinalerts.models._

object ExchangeCurrencyParsers {

  import CommonParsers._

  val parseCurrencyId = int("currency_id").map(ExchangeCurrencyId.apply)
  val parseExchange = str("exchange").map(Exchange.fromDatabaseString)
  val parseMarket = str("market").map(Market.apply)
  val parseCurrency = str("currency").map(Currency.apply)
  val parseCurrencyName = str("currency_name")(citextToString)
      .map(CurrencyName.apply).?
      .map { _.filter(_.string.nonEmpty) }

  val parseExchangeCurrency = (
      parseCurrencyId ~
          parseExchange ~
          parseMarket ~
          parseCurrency ~
          parseCurrencyName).map {

    case id ~ exchange ~ market ~ currency ~ currencyName =>
      ExchangeCurrency(id, exchange, market, currency, currencyName)
  }
}
