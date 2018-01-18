package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser.{bool, get, long}
import anorm.~
import com.alexitc.coinalerts.models.{FixedPriceAlert, FixedPriceAlertId, FixedPriceAlertWithCurrency}

object FixedPriceAlertParsers {

  import CommonParsers._
  import ExchangeCurrencyParsers._
  import UserParsers._

  val parseFixedPriceAlertId = long("fixed_price_alert_id").map(FixedPriceAlertId.apply)
  val parseisGreaterThan = bool("is_greater_than")
  val parsePrice = get[BigDecimal]("price")
  val parseBasePrice = get[BigDecimal]("base_price")

  val parseFixedPriceAlert = (
      parseFixedPriceAlertId ~
          parseUserId ~
          parseCurrencyId ~
          parseisGreaterThan ~
          parsePrice ~
          parseBasePrice.? ~
          parseCreatedOn).map {

    case alertId ~ userId ~ currencyId ~ isGreaterThan ~ price ~ basePrice ~ createdOn =>
      FixedPriceAlert(alertId, userId, currencyId, isGreaterThan, price, basePrice, createdOn)
  }

  val parseFixedPriceAlertWithCurrency = (
      parseFixedPriceAlertId ~
          parseUserId ~
          parseCurrencyId ~
          parseExchange ~
          parseMarket ~
          parseCurrency ~
          parseisGreaterThan ~
          parsePrice ~
          parseBasePrice.? ~
          parseCreatedOn).map {

    case alertId ~ userId ~ exchangeCurrencyId ~ exchange ~ market ~ currency ~ isGreaterThan ~ price ~ basePrice ~ createdOn =>
      FixedPriceAlertWithCurrency(alertId, userId, exchangeCurrencyId, exchange, market, currency, isGreaterThan, price, basePrice, createdOn)
  }
}
