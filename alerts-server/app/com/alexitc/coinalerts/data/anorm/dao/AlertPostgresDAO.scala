package com.alexitc.coinalerts.data.anorm.dao

import java.sql.Connection

import anorm._
import com.alexitc.coinalerts.data.anorm.AnormParsers._
import com.alexitc.coinalerts.models._

class AlertPostgresDAO {
  
  def create(createAlertModel: CreateAlertModel, userId: UserId)(implicit conn: Connection): Alert = {
    SQL(
      """
        |INSERT INTO alerts
        |  (alert_type, user_id, book, market, is_greater_than, price)
        |VALUES
        |  ({alert_type}, {user_id}, {book}, {market}, {is_greater_than}, {price})
        |RETURNING
        |  alert_id, alert_type, user_id, book, market, is_greater_than, price
      """.stripMargin
    ).on(
      "alert_type" -> createAlertModel.alertType.string,
      "user_id" -> userId.string,
      "book" -> createAlertModel.book.string,
      "market" -> createAlertModel.market.string,
      "is_greater_than" -> createAlertModel.isGreaterThan,
      "price" -> createAlertModel.price
    ).as(parseAlert.single)
  }

  def createBasePrice(alertId: AlertId, basePrice: BigDecimal)(implicit conn: Connection): BasePriceAlert = {
    SQL(
      """
        |INSERT INTO base_price_alerts
        |  (alert_id, base_price)
        |VALUES
        |  ({alert_id}, {base_price})
        |RETURNING
        |  alert_id, base_price
      """.stripMargin
    ).on(
      "alert_id" -> alertId.long,
      "base_price" -> basePrice
    ).as(parseBasePriceAlert.single)
  }
}
