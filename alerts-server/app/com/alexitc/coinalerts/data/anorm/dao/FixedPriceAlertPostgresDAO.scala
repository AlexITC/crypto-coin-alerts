package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.core.{Count, PaginatedQuery}
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.models._

class FixedPriceAlertPostgresDAO {
  
  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId)(implicit conn: Connection): FixedPriceAlert = {
    SQL(
      """
        |INSERT INTO fixed_price_alerts
        |  (user_id, book, market, is_greater_than, price, base_price)
        |VALUES
        |  ({user_id}, {book}, {market}, {is_greater_than}, {price}, {base_price})
        |RETURNING
        |  fixed_price_alert_id, user_id, book, market, is_greater_than, price, base_price
      """.stripMargin
    ).on(
      "user_id" -> userId.string,
      "book" -> createAlertModel.book.string,
      "market" -> createAlertModel.market.string,
      "is_greater_than" -> createAlertModel.isGreaterThan,
      "price" -> createAlertModel.price,
      "base_price" -> createAlertModel.basePrice
    ).as(parseAlert.single)
  }

  def markAsTriggered(alertId: FixedPriceAlertId)(implicit conn: Connection): Int = {
    SQL(
      """
        |UPDATE fixed_price_alerts
        |SET triggered_on = NOW()
        |WHERE triggered_on IS NULL AND
        |      fixed_price_alert_id = {fixed_price_alert_id}
      """.stripMargin
    ).on(
      "fixed_price_alert_id" -> alertId.long
    ).executeUpdate()
  }

  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal)(implicit conn: Connection): List[FixedPriceAlert] = {
    SQL(
      """
        |SELECT fixed_price_alert_id, user_id, book, market, is_greater_than, price, base_price
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

  def getAlerts(userId: UserId, query: PaginatedQuery)(implicit conn: Connection): List[FixedPriceAlert] = {
    SQL(
      s"""
         |SELECT fixed_price_alert_id, user_id, book, market, is_greater_than, price, base_price
         |FROM fixed_price_alerts
         |WHERE user_id = {user_id}
         |ORDER BY fixed_price_alert_id
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
