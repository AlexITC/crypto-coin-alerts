package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser.long
import anorm.~
import com.alexitc.coinalerts.models.{DailyPriceAlert, DailyPriceAlertId}

object DailyPriceAlertParsers {

  import CommonParsers._
  import ExchangeCurrencyParsers._
  import UserParsers._

  val parseDailyPriceAlertId = long("daily_price_alert_id").map(DailyPriceAlertId.apply)

  val parseDailyPriceAlert = (parseDailyPriceAlertId ~ parseUserId ~ parseCurrencyId ~ parseCreatedOn).map {
    case id ~ userId ~ currencyId ~ createdOn =>
      DailyPriceAlert(id, userId, currencyId, createdOn)
  }
}
