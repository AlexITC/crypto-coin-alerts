package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.data.async.DailyPriceAlertFutureDataHandler
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import com.alexitc.coinalerts.services.validators.DailyPriceAlertValidator

import scala.concurrent.ExecutionContext

class DailyPriceAlertService @Inject() (
    dailyPriceAlertValidator: DailyPriceAlertValidator,
    dailyPriceAlertsFutureDataHandler: DailyPriceAlertFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): FutureApplicationResult[DailyPriceAlert] = {
    val result = for {
      validatedModel <- dailyPriceAlertValidator.validate(createDailyPriceAlert).toFutureOr
      dailyPriceAlert <- dailyPriceAlertsFutureDataHandler.create(userId, validatedModel).toFutureOr
    } yield dailyPriceAlert

    result.toFuture
  }
}
