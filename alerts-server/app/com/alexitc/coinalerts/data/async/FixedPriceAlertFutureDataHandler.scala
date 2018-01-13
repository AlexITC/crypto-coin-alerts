package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.core.{Count, FuturePaginatedResult, PaginatedQuery}
import com.alexitc.coinalerts.data.{FixedPriceAlertBlockingDataHandler, FixedPriceAlertDataHandler}
import com.alexitc.coinalerts.models._

import scala.concurrent.Future

class FixedPriceAlertFutureDataHandler @Inject() (
    blockingDataHandler: FixedPriceAlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends FixedPriceAlertDataHandler[FutureApplicationResult] {

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): FutureApplicationResult[FixedPriceAlertWithCurrency] = Future {
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

  override def getAlerts(userId: UserId, query: PaginatedQuery): FuturePaginatedResult[FixedPriceAlertWithCurrency] = Future {
    blockingDataHandler.getAlerts(userId, query)
  }

  override def countBy(userId: UserId): FutureApplicationResult[Count] = Future {
    blockingDataHandler.countBy(userId)
  }
}
