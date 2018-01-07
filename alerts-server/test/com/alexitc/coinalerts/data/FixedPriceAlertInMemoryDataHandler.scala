package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.{ApplicationResult, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.errors.{FixedPriceAlertNotFoundError, UnknownExchangeCurrencyIdError}
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait FixedPriceAlertInMemoryDataHandler extends FixedPriceAlertBlockingDataHandler {

  // override this to check existing currencies
  def exchangeCurrencyBlocingDataHandler: Option[ExchangeCurrencyBlockingDataHandler] = None

  private val alertList = mutable.ListBuffer[FixedPriceAlert]()
  private val triggeredAlertList = mutable.ListBuffer[FixedPriceAlertId]()

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): ApplicationResult[FixedPriceAlert] = alertList.synchronized {
    if (allowCurrency(createAlertModel.exchangeCurrencyId)) {
      val alert = FixedPriceAlert(
        RandomDataGenerator.alertId,
        userId,
        createAlertModel.exchangeCurrencyId,
        createAlertModel.isGreaterThan,
        createAlertModel.price,
        createAlertModel.basePrice)

      alertList += alert

      Good(alert)
    } else {
      Bad(UnknownExchangeCurrencyIdError).accumulating
    }
  }

  private def allowCurrency(exchangeCurrencyId: ExchangeCurrencyId): Boolean = {
    exchangeCurrencyBlocingDataHandler.forall {
      _.getBy(exchangeCurrencyId).get.isDefined
    }
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): ApplicationResult[Unit] = alertList.synchronized {
    if (triggeredAlertList.contains(alertId)) {
      Bad(FixedPriceAlertNotFoundError).accumulating
    } else {
      triggeredAlertList += alertId
      Good(())
    }
  }

  override def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): ApplicationResult[List[FixedPriceAlert]] = alertList.synchronized {
    val list = pendingAlertList
        .filter(_.exchangeCurrencyId == currencyId)
        .filter { alert =>
          (alert.isGreaterThan && currentPrice >= alert.price) ||
              (!alert.isGreaterThan && currentPrice <= alert.price)
        }

    Good(list)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlert]] = alertList.synchronized {
    val userAlerts = alertList.toList.filter(_.userId == userId)
    val data = userAlerts.slice(query.offset.int, query.offset.int + query.limit.int)
    val result = PaginatedResult(query.offset, query.limit, Count(userAlerts.length), data)
    Good(result)
  }

  override def countBy(userId: UserId): ApplicationResult[Count] = alertList.synchronized {
    val result = Count(alertList.toList.count(_.userId == userId))
    Good(result)
  }

  private def pendingAlertList = {
    alertList.toList.filter { alert => !triggeredAlertList.contains(alert.id) }
  }
}
