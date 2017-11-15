package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait AlertDataHandler[F[_]] {

  def create(createAlertModel: CreateAlertModel, userId: UserId): F[Alert]

  def markAsTriggered(alertId: AlertId): F[Unit]
  
  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): F[List[Alert]]

  def findBasePriceAlert(alertId: AlertId): F[BasePriceAlert]
}

trait AlertBlockingDataHandler extends AlertDataHandler[ApplicationResult]
