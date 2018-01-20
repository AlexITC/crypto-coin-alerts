package com.alexitc.coinalerts.data.anorm.parsers

import anorm.SqlParser._
import anorm._
import com.alexitc.coinalerts.models.{NewCurrencyAlert, NewCurrencyAlertId}

object NewCurrencyAlertParsers {

  import ExchangeCurrencyParsers._
  import UserParsers._

  val parseNewCurrencyAlertId = int("new_currency_alert_id").map(NewCurrencyAlertId.apply)

  val parseNewCurrencyAlert = (parseNewCurrencyAlertId ~ parseUserId ~ parseExchange).map {
    case id ~ userId ~ exchange => NewCurrencyAlert(id, userId, exchange)
  }
}
