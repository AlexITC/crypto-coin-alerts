package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.core.{Count, PaginatedQuery}
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.models._

class AlertPostgresDAO {
  
  def create(createAlertModel: CreateAlertModel, userId: UserId)(implicit conn: Connection): Alert = {
    SQL(
      """
        |INSERT INTO fixed_price_alerts
        |  (alert_type, user_id, book, market, is_greater_than, price, base_price)
        |VALUES
        |  ({alert_type}, {user_id}, {book}, {market}, {is_greater_than}, {price}, {base_price})
        |RETURNING
        |  alert_id, alert_type, user_id, book, market, is_greater_than, price, base_price
      """.stripMargin
    ).on(
      "alert_type" -> createAlertModel.alertType.string,
      "user_id" -> userId.string,
      "book" -> createAlertModel.book.string,
      "market" -> createAlertModel.market.string,
      "is_greater_than" -> createAlertModel.isGreaterThan,
      "price" -> createAlertModel.price,
      "base_price" -> createAlertModel.basePrice
    ).as(parseAlert.single)
  }

  def markAsTriggered(alertId: AlertId)(implicit conn: Connection): Int = {
    SQL(
      """
        |UPDATE fixed_price_alerts
        |SET triggered_on = NOW()
        |WHERE triggered_on IS NULL AND
        |      alert_id = {alert_id}
      """.stripMargin
    ).on(
      "alert_id" -> alertId.long
    ).executeUpdate()
  }

  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal)(implicit conn: Connection): List[Alert] = {
    SQL(
      """
        |SELECT alert_id, alert_type, user_id, book, market, is_greater_than, price, base_price
        |FROM fixed_price_alerts
        |WHERE triggered_on IS NULL AND
        |      market = {market} AND
        |      book = {book} AND
        |      (
        |        (is_greater_than = TRUE AND {current_price} >= price) OR
        |        (is_greater_than = FALSE AND {current_price} <= price)
        |      )
      """.stripMargin
    ).on(
      "market" -> market.string,
      "book" -> book.string,
      "current_price" -> currentPrice
    ).as(parseAlert.*)
  }

  def getAlerts(userId: UserId, query: PaginatedQuery)(implicit conn: Connection): List[Alert] = {
    SQL(
      s"""
         |SELECT alert_id, alert_type, user_id, book, market, is_greater_than, price, base_price
         |FROM fixed_price_alerts
         |WHERE user_id = {user_id}
         |ORDER BY alert_id
         |OFFSET {offset}
         |LIMIT {limit}
       """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "offset" -> query.offset.int,
      "limit" -> query.limit.int
    ).as(parseAlert.*)
  }

  def countAlerts(userId: UserId)(implicit conn: Connection): Count = {
    val result = SQL(
      """
        |SELECT COUNT(*)
        |FROM fixed_price_alerts
        |WHERE user_id = {user_id}
      """.stripMargin
    ).on(
      "user_id" -> userId.string
    ).as(SqlParser.scalar[Int].single)

    Count(result)
  }
}
