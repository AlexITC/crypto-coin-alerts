package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.{ApplicationResult, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.errors.{FixedPriceAlertNotFoundError, UnknownExchangeCurrencyIdError}
import com.alexitc.coinalerts.models.FixedPriceAlertFilter._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One, Or}

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

  override def getAlerts(conditions: Conditions, query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlertWithCurrency]] = alertList.synchronized {
    val filteredAlerts = filterBy(conditions)
    val data = filteredAlerts
        .slice(query.offset.int, query.offset.int + query.limit.int)
        .map { fixedPriceAlert =>
          FixedPriceAlertWithCurrency.from(fixedPriceAlert, getCurrency(fixedPriceAlert.exchangeCurrencyId))
        }

    val result = PaginatedResult(query.offset, query.limit, Count(filteredAlerts.length), data)
    Good(result)
  }

  override def countBy(conditions: Conditions): ApplicationResult[Count] = alertList.synchronized {
    val result = Count(filterBy(conditions).length)
    Good(result)
  }

  override def delete(id: FixedPriceAlertId, userId: UserId): ApplicationResult[FixedPriceAlertWithCurrency] = alertList.synchronized {
    val index = alertList.indexWhere { alert =>
      alert.id == id && alert.userId == userId
    }

    val alertMaybe = Option(index)
        .filter(_ != -1)
        .map(idx => alertList.remove(idx))
        .map { alert =>
          val currency = getCurrency(alert.exchangeCurrencyId)
          FixedPriceAlertWithCurrency.from(alert, currency)
        }

    Or.from(alertMaybe, One(FixedPriceAlertNotFoundError))
  }

  private def pendingAlertList = {
    alertList.toList.filter { alert => !triggeredAlertList.contains(alert.id) }
  }

  private def filterBy(conditions: Conditions) = {
    List(conditions.triggered, conditions.user).foldLeft(alertList.toList) { (list, condition) =>
      condition match {
        case JustThisUserCondition(userId) => list.filter(_.userId == userId)
        case HasBeenTriggeredCondition => list.filter(triggeredAlertList contains _.id)
        case HasNotBeenTriggeredCondition => list.filterNot(triggeredAlertList contains _.id)
        case _ => list
      }
    }
  }
}
