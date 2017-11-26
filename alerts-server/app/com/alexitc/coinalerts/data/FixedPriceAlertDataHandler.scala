package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait FixedPriceAlertDataHandler[F[_]] {

  def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): F[FixedPriceAlert]

  def markAsTriggered(alertId: FixedPriceAlertId): F[Unit]

  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): F[List[FixedPriceAlert]]

  def getAlerts(userId: UserId, query: PaginatedQuery): F[PaginatedResult[FixedPriceAlert]]
}

trait FixedPriceAlertBlockingDataHandler extends FixedPriceAlertDataHandler[ApplicationResult]
