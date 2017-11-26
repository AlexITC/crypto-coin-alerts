package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.async.AlertFutureDataHandler
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.{AlertValidator, PaginatedQueryValidator}

import scala.concurrent.ExecutionContext

class AlertService @Inject() (
    alertValidator: AlertValidator,
    paginatedQueryValidator: PaginatedQueryValidator,
    alertFutureDataHandler: AlertFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def create(createAlertModel: CreateAlertModel, userId: UserId): FutureApplicationResult[Alert] = {
    val result = for {
      validatedModel <- alertValidator.validateCreateAlertModel(createAlertModel).toFutureOr
      // TODO: Restrict the maximum number of active alerts per user
      // TODO: shall we restrict on repeated alerts by price and greater than?
      // TODO: shall we restrict on creating an alert that will be triggered right away?
      createdAlert <- alertFutureDataHandler.create(validatedModel, userId).toFutureOr
    } yield createdAlert

    result.toFuture
  }

  def getAlerts(userId: UserId, query: PaginatedQuery): FutureApplicationResult[PaginatedResult[Alert]] = {
    val result = for {
      validatedQuery <- paginatedQueryValidator.validate(query).toFutureOr
      paginatedResult <- alertFutureDataHandler.getAlerts(userId, validatedQuery).toFutureOr
    } yield paginatedResult

    result.toFuture
  }
}
