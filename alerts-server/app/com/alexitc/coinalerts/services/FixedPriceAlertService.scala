package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.commons.FutureOr.Implicits.{FutureOps, OrOps}
import com.alexitc.coinalerts.config.FixedPriceAlertConfig
import com.alexitc.coinalerts.core._
import com.alexitc.coinalerts.data.async.FixedPriceAlertFutureDataHandler
import com.alexitc.coinalerts.errors.TooManyFixedPriceAlertsError
import com.alexitc.coinalerts.models.FixedPriceAlertFilter.{HasNotBeenTriggeredCondition, JustThisUserCondition}
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.parsers.{FixedPriceAlertFilterParser, FixedPriceAlertOrderByParser}
import com.alexitc.coinalerts.services.validators.{FixedPriceAlertValidator, PaginatedQueryValidator}
import org.scalactic.{Bad, Good}

import scala.concurrent.ExecutionContext

class FixedPriceAlertService @Inject() (
    alertValidator: FixedPriceAlertValidator,
    paginatedQueryValidator: PaginatedQueryValidator,
    config: FixedPriceAlertConfig,
    alertFilterParser: FixedPriceAlertFilterParser,
    alertOrderByParser: FixedPriceAlertOrderByParser,
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

  def getAlerts(
      userId: UserId,
      query: PaginatedQuery,
      filterQuery: FilterQuery): FuturePaginatedResult[FixedPriceAlertWithCurrency] = {

    val result = for {
      validatedQuery <- paginatedQueryValidator.validate(query).toFutureOr
      filterConditions <- alertFilterParser.from(filterQuery, userId).toFutureOr
      paginatedResult <- alertFutureDataHandler.getAlerts(filterConditions, FixedPriceAlertOrderByParser.DefaultConditions, validatedQuery).toFutureOr
    } yield paginatedResult

    result.toFuture
  }

  def delete(id: FixedPriceAlertId, userId: UserId): FutureApplicationResult[FixedPriceAlertWithCurrency] = {
    alertFutureDataHandler.delete(id, userId)
  }

  private def enforceMaximunNumberOfAlerts(userId: UserId, maximumNumberOfAlerts: Count): FutureApplicationResult[Unit] = {
    val conditions = FixedPriceAlertFilter.Conditions(
      triggered = HasNotBeenTriggeredCondition,
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
