package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait FixedPriceAlertDataHandler[F[_]] {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): F[FixedPriceAlertWithCurrency]

  def markAsTriggered(alertId: FixedPriceAlertId): F[Unit]

  def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): F[List[FixedPriceAlertWithCurrency]]

  def getAlerts(userId: UserId, query: PaginatedQuery): F[PaginatedResult[FixedPriceAlertWithCurrency]]

  def countBy(userId: UserId): F[Count]
}

trait FixedPriceAlertBlockingDataHandler extends FixedPriceAlertDataHandler[ApplicationResult]
