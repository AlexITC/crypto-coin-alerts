package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.{ApplicationResult, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.errors.AlertNotFound
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait FixedPriceAlertInMemoryDataHandler extends FixedPriceAlertBlockingDataHandler {

  private val alertList = mutable.ListBuffer[FixedPriceAlert]()
  private val triggeredAlertList = mutable.ListBuffer[FixedPriceAlertId]()

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): ApplicationResult[FixedPriceAlert] = {
    val alert = FixedPriceAlert(
      RandomDataGenerator.alertId,
      userId,
      createAlertModel.market,
      createAlertModel.book,
      createAlertModel.isGreaterThan,
      createAlertModel.price,
      createAlertModel.basePrice)

    alertList += alert

    Good(alert)
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): ApplicationResult[Unit] = {
    if (triggeredAlertList.contains(alertId)) {
      Bad(AlertNotFound).accumulating
    } else {
      triggeredAlertList += alertId
      Good(())
    }
  }

  override def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): ApplicationResult[List[FixedPriceAlert]] = {
    val list = pendingAlertList
        .filter(_.market == market)
        .filter(_.book == book)
        .filter { alert =>
          (alert.isGreaterThan && currentPrice >= alert.price) ||
              (!alert.isGreaterThan && currentPrice <= alert.price)
        }

    Good(list)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlert]] = {
    val userAlerts = alertList.toList.filter(_.userId == userId)
    val data = userAlerts.slice(query.offset.int, query.offset.int + query.limit.int)
    val result = PaginatedResult(query.offset, query.limit, Count(userAlerts.length), data)
    Good(result)
  }

  private def pendingAlertList = {
    alertList.toList.filter { alert => !triggeredAlertList.contains(alert.id) }
  }
}
