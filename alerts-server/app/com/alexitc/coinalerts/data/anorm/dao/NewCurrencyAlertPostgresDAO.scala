package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.data.anorm.parsers.NewCurrencyAlertParsers
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, NewCurrencyAlertId, UserId}

class NewCurrencyAlertPostgresDAO {

  import NewCurrencyAlertParsers._

  /**
   * Creating an alert could fail because the exchange already exist for the given user.
   */
  def create(userId: UserId, exchange: Exchange)(implicit conn: Connection): Option[NewCurrencyAlert] = {
    SQL(
      """
        |INSERT INTO new_currency_alerts
        |  (user_id, exchange)
        |VALUES
        |  ({user_id}, {exchange})
        |ON CONFLICT (user_id, exchange) DO NOTHING
        |RETURNING new_currency_alert_id, user_id, exchange
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "exchange" -> exchange.string
    ).as(parseNewCurrencyAlert.singleOpt)
  }

  def get(userId: UserId)(implicit conn: Connection): List[NewCurrencyAlert] = {
    SQL(
      """
        |SELECT new_currency_alert_id, user_id, exchange
        |FROM new_currency_alerts
        |WHERE user_id = {user_id}
      """.stripMargin
    ).on(
      "user_id" -> userId.string
    ).as(parseNewCurrencyAlert.*)
  }

  def getAll(implicit conn: Connection): List[NewCurrencyAlert] = {
    SQL(
      """
        |SELECT new_currency_alert_id, user_id, exchange
        |FROM new_currency_alerts
      """.stripMargin
    ).as(parseNewCurrencyAlert.*)
  }

  def delete(id: NewCurrencyAlertId, userId: UserId)(implicit conn: Connection): Option[NewCurrencyAlert] = {
    SQL(
      """
        |DELETE FROM new_currency_alerts
        |WHERE new_currency_alert_id = {new_currency_alert_id} AND
        |      user_id = {user_id}
        |RETURNING new_currency_alert_id, user_id, exchange
      """.stripMargin
    ).on(
      "new_currency_alert_id" -> id.int,
      "user_id" -> userId.string
    ).as(parseNewCurrencyAlert.singleOpt)
  }
}
