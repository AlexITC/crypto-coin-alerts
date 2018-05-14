package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import com.alexitc.playsonify.core.ApplicationResult
import com.alexitc.playsonify.models.{PaginatedQuery, PaginatedResult}

import scala.language.higherKinds

trait DailyPriceAlertDataHandler[F[_]] {

  def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): F[DailyPriceAlert]

  def getAlerts(userId: UserId, query: PaginatedQuery): F[PaginatedResult[DailyPriceAlert]]
}

trait DailyPriceAlertBlockingDataHandler extends DailyPriceAlertDataHandler[ApplicationResult]
