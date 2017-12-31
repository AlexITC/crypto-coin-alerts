package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.data.anorm.AnormParsers
import com.alexitc.coinalerts.models.{Currency, Exchange, ExchangeCurrency, Market}

class ExchangeCurrencyPostgresDAO {

  def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency)(
      implicit conn: Connection): Option[ExchangeCurrency] = {

    SQL(
      """
        |SELECT currency_id, exchange, market, currency
        |FROM currencies
        |WHERE exchange = {exchange} AND
        |      market = {market} AND
        |      currency = {currency} AND
        |      deleted_on IS NULL
      """.stripMargin
    ).on(
      "exchange" -> exchange.string,
      "market" -> market.string,
      "currency" -> currency.string
    ).as(AnormParsers.parseExchangeCurrency.singleOpt)
  }
}
