package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.{FixedPriceAlertValidator, PaginatedQueryValidator}

import scala.concurrent.ExecutionContext

class FixedPriceAlertService @Inject() (
    alertValidator: FixedPriceAlertValidator,
    paginatedQueryValidator: PaginatedQueryValidator,
    alertFutureDataHandler: FixedPriceAlertFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): FutureApplicationResult[FixedPriceAlert] = {
    val result = for {
      validatedModel <- alertValidator.validateCreateModel(createAlertModel).toFutureOr
      // TODO: Restrict the maximum number of active alerts per user
      // TODO: shall we restrict on repeated alerts by price and greater than?
      // TODO: shall we restrict on creating an alert that will be triggered right away?
      createdAlert <- alertFutureDataHandler.create(validatedModel, userId).toFutureOr
    } yield createdAlert

    result.toFuture
  }

  def getAlerts(userId: UserId, query: PaginatedQuery): FutureApplicationResult[PaginatedResult[FixedPriceAlert]] = {
    val result = for {
      validatedQuery <- paginatedQueryValidator.validate(query).toFutureOr
      paginatedResult <- alertFutureDataHandler.getAlerts(userId, validatedQuery).toFutureOr
    } yield paginatedResult

    result.toFuture
  }
}
