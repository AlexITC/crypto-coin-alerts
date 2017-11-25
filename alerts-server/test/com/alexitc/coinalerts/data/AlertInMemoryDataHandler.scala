package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.RandomDataGenerator
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.AlertNotFound
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One, Or}

import scala.collection.mutable

trait AlertInMemoryDataHandler extends AlertBlockingDataHandler {

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

  override def findBasePriceAlert(alertId: AlertId): ApplicationResult[BasePriceAlert] = {
    val alertMaybe = basePriceAlert
        .get(alertId)
        .map { basePrice => BasePriceAlert(alertId, basePrice) }

    Or.from(alertMaybe, One(AlertNotFound))
  }

  private def pendingAlertList = {
    alertList.toList.filter { alert => !triggeredAlertList.contains(alert.id) }
  }
}
