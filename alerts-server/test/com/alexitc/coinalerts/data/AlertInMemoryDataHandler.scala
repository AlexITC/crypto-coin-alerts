package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.common.RandomDataGenerator
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._
import org.scalactic.Good

import scala.collection.mutable

trait AlertInMemoryDataHandler extends AlertDataHandler {

  private val alertList = mutable.ListBuffer[Alert]()
  private val basePriceAlert = mutable.HashMap[AlertId, BigDecimal]()

  override def create(createAlertModel: CreateAlertModel, userId: UserId): ApplicationResult[Alert] = {
    val alert = Alert(
      RandomDataGenerator.alertId,
      createAlertModel.alertType,
      userId,
      createAlertModel.market,
      createAlertModel.book,
      createAlertModel.isGreaterThan,
      createAlertModel.price)

    alertList += alert

    if (alert.alertType == AlertType.BASE_PRICE) {
      basePriceAlert += alert.id -> createAlertModel.basePrice.get
    }

    Good(alert)
  }
}
