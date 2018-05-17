package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.data.anorm.parsers.ExchangeCurrencyParsers
import com.alexitc.coinalerts.models.{CreateExchangeCurrencyModel, _}

class ExchangeCurrencyPostgresDAO {

  import ExchangeCurrencyParsers._

  def create(
      createModel: CreateExchangeCurrencyModel)(
      implicit conn: Connection): Option[ExchangeCurrency] = {

    SQL(
      """
        |INSERT INTO currencies
        |  (exchange, market, currency, currency_name)
        |VALUES
        |  ({exchange}, {market}, {currency}, {currency_name})
        |ON CONFLICT DO NOTHING
        |RETURNING currency_id, exchange, market, currency, currency_name
      """.stripMargin
    ).on(
      "exchange" -> createModel.exchange.string,
      "market" -> createModel.market.string,
      "currency" -> createModel.currency.string,
      "currency_name" -> createModel.currencyName.map(_.string).getOrElse("")
    ).as(parseExchangeCurrency.singleOpt).flatten
  }

  def getBy(
      exchangeCurrencyId: ExchangeCurrencyId)(
      implicit conn: Connection): Option[ExchangeCurrency] = {

    SQL(
      """
        |SELECT currency_id, exchange, market, currency, currency_name
        |FROM currencies
        |WHERE currency_id = {currency_id}
      """.stripMargin
    ).on(
      "currency_id" -> exchangeCurrencyId.int
    ).as(parseExchangeCurrency.singleOpt).flatten
  }

  def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency,
      currencyName: CurrencyName)(
      implicit conn: Connection): Option[ExchangeCurrency] = {

    SQL(
      """
        |SELECT currency_id, exchange, market, currency, currency_name
        |FROM currencies
        |WHERE exchange = {exchange} AND
        |      market = {market} AND
        |      currency = {currency} AND
        |      currency_name = {currency_name} AND
        |      deleted_on IS NULL
      """.stripMargin
    ).on(
      "exchange" -> exchange.string,
      "market" -> market.string,
      "currency" -> currency.string,
      "currency_name" -> currencyName.string
    ).as(parseExchangeCurrency.singleOpt).flatten
  }

  def getBy(
      exchange: Exchange,
      market: Market)(
      implicit conn: Connection): List[ExchangeCurrency] = {

    SQL(
      """
        |SELECT currency_id, exchange, market, currency, currency_name
        |FROM currencies
        |WHERE exchange = {exchange} AND
        |      market = {market}
      """.stripMargin
    ).on(
      "exchange" -> exchange.string,
      "market" -> market.string,
    ).as(parseExchangeCurrency.*).flatten
  }

  def getMarkets(exchange: Exchange)(implicit conn: Connection): List[Market] = {
    SQL(
      """
        |SELECT DISTINCT market
        |FROM currencies
        |WHERE exchange = {exchange}
      """.stripMargin
    ).on(
      "exchange" -> exchange.string
    ).as(parseMarket.*).flatten
  }

  /**
   * The total amount of currencies should not be too big to retrieve
   * them all at once, in case the number gets too big, we can get
   * all currencies for a exchange.
   */
  def getAll(implicit conn: Connection): List[ExchangeCurrency] = {
    SQL(
      """
        |SELECT currency_id, exchange, market, currency, currency_name
        |FROM currencies
      """.stripMargin
    ).as(parseExchangeCurrency.*).flatten
  }
}
