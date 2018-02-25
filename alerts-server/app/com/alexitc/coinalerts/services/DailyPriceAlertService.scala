package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.core.{FuturePaginatedResult, PaginatedQuery}
import com.alexitc.coinalerts.data.async.DailyPriceAlertFutureDataHandler
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import com.alexitc.coinalerts.services.validators.PaginatedQueryValidator
import com.alexitc.playsonify.core.FutureApplicationResult
import com.alexitc.playsonify.core.FutureOr.Implicits.{FutureOps, OrOps}

import scala.concurrent.ExecutionContext

class DailyPriceAlertService @Inject() (
    queryValidator: PaginatedQueryValidator,
    dailyPriceAlertsFutureDataHandler: DailyPriceAlertFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): FutureApplicationResult[DailyPriceAlert] = {
    val result = for {
      // there is nothing to validate
      dailyPriceAlert <- dailyPriceAlertsFutureDataHandler.create(userId, createDailyPriceAlert).toFutureOr
    } yield dailyPriceAlert

    result.toFuture
  }

  def getAlerts(userId: UserId, query: PaginatedQuery): FuturePaginatedResult[DailyPriceAlert] = {
    val result = for {
      validatedQuery <- queryValidator.validate(query).toFutureOr

      paginatedResult <- dailyPriceAlertsFutureDataHandler
          .getAlerts(userId, validatedQuery)
          .toFutureOr
    } yield paginatedResult

    result.toFuture
  }
}
