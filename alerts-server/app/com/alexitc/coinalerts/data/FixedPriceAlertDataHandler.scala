package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.models.FixedPriceAlertFilter.Conditions
import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.models.fields.FixedPriceAlertField
import com.alexitc.playsonify.core.ApplicationResult
import com.alexitc.playsonify.models.{Count, FieldOrdering, PaginatedQuery, PaginatedResult}

import scala.language.higherKinds

trait FixedPriceAlertDataHandler[F[_]] {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): F[FixedPriceAlertWithCurrency]

  def markAsTriggered(alertId: FixedPriceAlertId): F[Unit]

  def findPendingAlertsForPrice(
      currencyId: ExchangeCurrencyId,
      currentPrice: BigDecimal): F[List[FixedPriceAlertWithCurrency]]

  def getAlerts(
      filterConditions: FixedPriceAlertFilter.Conditions,
      orderByConditions: FieldOrdering[FixedPriceAlertField],
      query: PaginatedQuery): F[PaginatedResult[FixedPriceAlertWithCurrency]]

  def countBy(conditions: Conditions): F[Count]

  def delete(id: FixedPriceAlertId, userId: UserId): F[FixedPriceAlertWithCurrency]
}

trait FixedPriceAlertBlockingDataHandler extends FixedPriceAlertDataHandler[ApplicationResult]
