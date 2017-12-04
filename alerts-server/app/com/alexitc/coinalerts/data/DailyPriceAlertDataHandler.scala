package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}

import scala.language.higherKinds

trait DailyPriceAlertDataHandler[F[_]] {

  def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): F[DailyPriceAlert]
}

trait DailyPriceAlertBlockingDataHandler extends DailyPriceAlertDataHandler[ApplicationResult]
