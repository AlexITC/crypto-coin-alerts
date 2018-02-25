package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.core.{FuturePaginatedResult, PaginatedQuery}
import com.alexitc.coinalerts.data.{DailyPriceAlertBlockingDataHandler, DailyPriceAlertDataHandler}
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import com.alexitc.playsonify.core.FutureApplicationResult

import scala.concurrent.Future

class DailyPriceAlertFutureDataHandler @Inject() (
    dailyPriceAlertBlockingDataHandler: DailyPriceAlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends DailyPriceAlertDataHandler[FutureApplicationResult] {

  override def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): FutureApplicationResult[DailyPriceAlert] = Future {
    dailyPriceAlertBlockingDataHandler.create(userId, createDailyPriceAlert)
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): FuturePaginatedResult[DailyPriceAlert] = Future {
    dailyPriceAlertBlockingDataHandler.getAlerts(userId, query)
  }
}
