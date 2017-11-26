package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.{AlertBlockingDataHandler, AlertDataHandler}
import com.alexitc.coinalerts.models._

import scala.concurrent.Future

class AlertFutureDataHandler @Inject() (
    blockingDataHandler: AlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends AlertDataHandler[FutureApplicationResult] {

  override def create(createAlertModel: CreateAlertModel, userId: UserId): FutureApplicationResult[Alert] = Future {
    blockingDataHandler.create(createAlertModel, userId)
  }

  override def markAsTriggered(alertId: AlertId): FutureApplicationResult[Unit] = Future {
    blockingDataHandler.markAsTriggered(alertId)
  }

  override def findPendingAlertsForPrice(
      market: Market,
      book: Book,
      currentPrice: BigDecimal): FutureApplicationResult[List[Alert]] = Future {

    blockingDataHandler.findPendingAlertsForPrice(market, book, currentPrice)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): FutureApplicationResult[PaginatedResult[Alert]] = Future {
    blockingDataHandler.getAlerts(userId, query)
  }
}
