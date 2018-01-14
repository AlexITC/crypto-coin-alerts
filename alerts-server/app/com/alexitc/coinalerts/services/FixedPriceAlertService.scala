package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.config.FixedPriceAlertConfig
import com.alexitc.coinalerts.core.{Count, FuturePaginatedResult, PaginatedQuery}
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.errors.TooManyFixedPriceAlertsError
import com.alexitc.coinalerts.models.FixedPriceAlertFilter.{AnyTriggeredCondition, JustThisUserCondition}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.validators.{FixedPriceAlertValidator, PaginatedQueryValidator}
import org.scalactic.{Bad, Good}

import scala.concurrent.ExecutionContext

class FixedPriceAlertService @Inject() (
    alertValidator: FixedPriceAlertValidator,
    paginatedQueryValidator: PaginatedQueryValidator,
    config: FixedPriceAlertConfig,
    alertFutureDataHandler: FixedPriceAlertFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): FutureApplicationResult[FixedPriceAlertWithCurrency] = {
    val result = for {
      validatedModel <- alertValidator.validateCreateModel(createAlertModel).toFutureOr

      // TODO: shall we restrict on repeated alerts by price and greater than?
      // TODO: shall we restrict on creating an alert that will be triggered right away?
      _ <- enforceMaximunNumberOfAlerts(userId, config.maximumNumberOfAlertsPerUser).toFutureOr
      createdAlert <- alertFutureDataHandler.create(validatedModel, userId).toFutureOr
    } yield createdAlert

    result.toFuture
  }

  def getAlerts(userId: UserId, query: PaginatedQuery): FuturePaginatedResult[FixedPriceAlertWithCurrency] = {
    val conditions = FixedPriceAlertFilter.Conditions(
      triggered = AnyTriggeredCondition,
      user = JustThisUserCondition(userId))

    val result = for {
      validatedQuery <- paginatedQueryValidator.validate(query).toFutureOr
      paginatedResult <- alertFutureDataHandler.getAlerts(conditions, validatedQuery).toFutureOr
    } yield paginatedResult

    result.toFuture
  }

  // TODO: Check triggered alerts only
  private def enforceMaximunNumberOfAlerts(userId: UserId, maximumNumberOfAlerts: Count): FutureApplicationResult[Unit] = {
    val conditions = FixedPriceAlertFilter.Conditions(
      triggered = AnyTriggeredCondition,
      user = JustThisUserCondition(userId))

    val result = for {
      numberOfAlerts <- alertFutureDataHandler.countBy(conditions).toFutureOr
    } yield numberOfAlerts.int match {
      case x if x >= maximumNumberOfAlerts.int =>
        Bad(TooManyFixedPriceAlertsError(maximumNumberOfAlerts)).accumulating

      case _ =>
        Good(())
    }

    result
        .mapWithError(identity)
        .toFuture
  }
}
