package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.{FixedPriceAlertBlockingDataHandler, FixedPriceAlertDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.models.fields.FixedPriceAlertField
import com.alexitc.playsonify.core.{FutureApplicationResult, FuturePaginatedResult}
import com.alexitc.playsonify.models.{Count, FieldOrdering, PaginatedQuery}

import scala.concurrent.Future

class FixedPriceAlertFutureDataHandler @Inject()(blockingDataHandler: FixedPriceAlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends FixedPriceAlertDataHandler[FutureApplicationResult] {

  override def create(
      createAlertModel: CreateFixedPriceAlertModel,
      userId: UserId): FutureApplicationResult[FixedPriceAlertWithCurrency] = Future {
    blockingDataHandler.create(createAlertModel, userId)
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): FutureApplicationResult[Unit] = Future {
    blockingDataHandler.markAsTriggered(alertId)
  }

  override def findPendingAlertsForPrice(
      currencyId: ExchangeCurrencyId,
      currentPrice: BigDecimal): FutureApplicationResult[List[FixedPriceAlertWithCurrency]] = Future {

    blockingDataHandler.findPendingAlertsForPrice(currencyId, currentPrice)
  }

  override def getAlerts(
      filterConditions: FixedPriceAlertFilter.Conditions,
      orderByConditions: FieldOrdering[FixedPriceAlertField],
      query: PaginatedQuery): FuturePaginatedResult[FixedPriceAlertWithCurrency] = Future {

    blockingDataHandler.getAlerts(filterConditions, orderByConditions, query)
  }

  override def countBy(conditions: FixedPriceAlertFilter.Conditions): FutureApplicationResult[Count] = Future {
    blockingDataHandler.countBy(conditions)
  }

  override def delete(id: FixedPriceAlertId, userId: UserId): FutureApplicationResult[FixedPriceAlertWithCurrency] =
    Future {
      blockingDataHandler.delete(id, userId)
    }
}
