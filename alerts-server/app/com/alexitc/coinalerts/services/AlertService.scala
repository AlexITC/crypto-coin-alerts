package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.data.async.AlertAsyncDataHandler
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.AlertValidator

import scala.concurrent.ExecutionContext

class AlertService @Inject() (
    alertValidator: AlertValidator,
    alertAsyncDataHandler: AlertAsyncDataHandler)(
    implicit ec: ExecutionContext) {

  def create(createAlertModel: CreateAlertModel, userId: UserId): FutureApplicationResult[Alert] = {
    val result = for {
      validatedModel <- alertValidator.validateCreateAlertModel(createAlertModel).toFutureOr
      // TODO: Restrict the maximum number of active alerts per user
      // TODO: shall we restrict on repeated alerts by price and greater than?
      // TODO: shall we restrict on creating an alert that will be triggered right away?
      createdAlert <- alertAsyncDataHandler.create(validatedModel, userId).toFutureOr
    } yield createdAlert

    result.toFuture
  }
}
