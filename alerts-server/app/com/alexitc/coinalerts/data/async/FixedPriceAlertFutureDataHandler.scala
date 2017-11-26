package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.{FixedPriceAlertBlockingDataHandler, FixedPriceAlertDataHandler}
import com.alexitc.coinalerts.models._

import scala.concurrent.Future

class FixedPriceAlertFutureDataHandler @Inject() (
    blockingDataHandler: FixedPriceAlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends FixedPriceAlertDataHandler[FutureApplicationResult] {

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): FutureApplicationResult[FixedPriceAlert] = Future {
    blockingDataHandler.create(createAlertModel, userId)
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): FutureApplicationResult[Unit] = Future {
    blockingDataHandler.markAsTriggered(alertId)
  }

  override def findPendingAlertsForPrice(
      market: Market,
      book: Book,
      currentPrice: BigDecimal): FutureApplicationResult[List[FixedPriceAlert]] = Future {

    blockingDataHandler.findPendingAlertsForPrice(market, book, currentPrice)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): FutureApplicationResult[PaginatedResult[FixedPriceAlert]] = Future {
    blockingDataHandler.getAlerts(userId, query)
  }
}
