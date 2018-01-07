package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait FixedPriceAlertDataHandler[F[_]] {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): F[FixedPriceAlert]

  def markAsTriggered(alertId: FixedPriceAlertId): F[Unit]

  def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): F[List[FixedPriceAlert]]

  def getAlerts(userId: UserId, query: PaginatedQuery): F[PaginatedResult[FixedPriceAlert]]

  def countBy(userId: UserId): F[Count]
}

trait FixedPriceAlertBlockingDataHandler extends FixedPriceAlertDataHandler[ApplicationResult]
