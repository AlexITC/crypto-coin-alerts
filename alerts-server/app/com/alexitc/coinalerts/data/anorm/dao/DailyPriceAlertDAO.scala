package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.anorm.AnormParsers
import com.alexitc.coinalerts.errors.RepeatedDailyPriceAlertError
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import org.scalactic.{One, Or}

class DailyPriceAlertDAO {

  def create(
      userId: UserId,
      createDailyPriceAlert: CreateDailyPriceAlertModel)(
      implicit conn: Connection): ApplicationResult[DailyPriceAlert] = {

    val alertMaybe = SQL(
      """
        |INSERT INTO daily_price_alerts
        |  (user_id, market, book)
        |VALUES
        |  ({user_id}, {market}, {book})
        |ON CONFLICT DO NOTHING
        |RETURNING daily_price_alert_id, user_id, market, book, created_on
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "market" -> createDailyPriceAlert.market.string,
      "book" -> createDailyPriceAlert.book.string
    ).as(AnormParsers.parseDailyPriceAlert.singleOpt)

    Or.from(alertMaybe, One(RepeatedDailyPriceAlertError))
  }
}
