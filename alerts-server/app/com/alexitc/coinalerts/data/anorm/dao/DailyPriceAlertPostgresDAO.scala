package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{Count, PaginatedQuery}
import com.alexitc.coinalerts.data.anorm.AnormParsers
import com.alexitc.coinalerts.errors.RepeatedDailyPriceAlertError
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import org.scalactic.{One, Or}

class DailyPriceAlertPostgresDAO {

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

  def getAlerts(
      userId: UserId,
      query: PaginatedQuery)(
      implicit conn: Connection): List[DailyPriceAlert] = {

    SQL(
      """
        |SELECT daily_price_alert_id, user_id, market, book, created_on
        |FROM daily_price_alerts
        |WHERE user_id = {user_id}
        |ORDER BY daily_price_alert_id
        |OFFSET {offset}
        |LIMIT {limit}
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "offset" -> query.offset.int,
      "limit" -> query.limit.int
    ).as(AnormParsers.parseDailyPriceAlert.*)
  }

  def countAlerts(
      userId: UserId)(
      implicit conn: Connection): Count = {

    val result: Int = SQL(
      """
        |SELECT COUNT(*)
        |FROM daily_price_alerts
        |WHERE user_id = {user_id}
      """.stripMargin
    ).on(
      "user_id" -> userId.string
    ).as(SqlParser.scalar[Int].single)

    Count(result)
  }
}
