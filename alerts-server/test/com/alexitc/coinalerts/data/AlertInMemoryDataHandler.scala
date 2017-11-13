package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.common.RandomDataGenerator
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.AlertNotFound
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait AlertInMemoryDataHandler extends AlertDataHandler {

  private val alertList = mutable.ListBuffer[Alert]()
  private val basePriceAlert = mutable.HashMap[AlertId, BigDecimal]()
  private val triggeredAlertList = mutable.ListBuffer[AlertId]()

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

  override def markAsTriggered(alertId: AlertId): ApplicationResult[Unit] = {
    if (triggeredAlertList.contains(alertId)) {
      Bad(AlertNotFound).accumulating
    } else {
      triggeredAlertList += alertId
      Good(())
    }
  }

  override def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): ApplicationResult[List[Alert]] = {
    val list = pendingAlertList
        .filter(_.market == market)
        .filter(_.book == book)
        .filter { alert =>
          (alert.isGreaterThan && currentPrice >= alert.price) ||
              (!alert.isGreaterThan && currentPrice <= alert.price)
        }

    Good(list)
  }

  private def pendingAlertList = {
    alertList.toList.filter { alert => !triggeredAlertList.contains(alert.id) }
  }
}
