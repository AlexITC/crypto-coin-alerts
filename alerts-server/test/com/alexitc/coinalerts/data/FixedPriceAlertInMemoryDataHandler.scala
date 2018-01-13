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

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): ApplicationResult[FixedPriceAlertWithCurrency] = alertList.synchronized {
    if (allowCurrency(createAlertModel.exchangeCurrencyId)) {
      val fixedPriceAlert = FixedPriceAlert(
        RandomDataGenerator.alertId,
        userId,
        createAlertModel.exchangeCurrencyId,
        createAlertModel.isGreaterThan,
        createAlertModel.price,
        createAlertModel.basePrice)

      alertList += fixedPriceAlert

      val exchangeCurrency = getCurrency(createAlertModel.exchangeCurrencyId)
      val alert = FixedPriceAlertWithCurrency.from(fixedPriceAlert, exchangeCurrency)
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

  /**
   * get currency from the data handler if available, else get random values
   */
  private def getCurrency(id: ExchangeCurrencyId): ExchangeCurrency = {
    exchangeCurrencyBlocingDataHandler
        .map(_.getBy(id).get.get)
        .getOrElse(RandomDataGenerator.exchangeCurrency(id))
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): ApplicationResult[Unit] = alertList.synchronized {
    if (triggeredAlertList.contains(alertId)) {
      Bad(FixedPriceAlertNotFoundError).accumulating
    } else {
      triggeredAlertList += alertId
      Good(())
    }
  }

  override def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): ApplicationResult[List[FixedPriceAlertWithCurrency]] = alertList.synchronized {
    val list = pendingAlertList
        .filter(_.exchangeCurrencyId == currencyId)
        .filter { alert =>
          (alert.isGreaterThan && currentPrice >= alert.price) ||
              (!alert.isGreaterThan && currentPrice <= alert.price)
        }
        .map { fixedPriceAlert =>
          FixedPriceAlertWithCurrency.from(fixedPriceAlert, getCurrency(currencyId))
        }

    Good(list)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlertWithCurrency]] = alertList.synchronized {
    val userAlerts = alertList.toList.filter(_.userId == userId)
    val data = userAlerts
        .slice(query.offset.int, query.offset.int + query.limit.int)
        .map { fixedPriceAlert =>
          FixedPriceAlertWithCurrency.from(fixedPriceAlert, getCurrency(fixedPriceAlert.exchangeCurrencyId))
        }

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
