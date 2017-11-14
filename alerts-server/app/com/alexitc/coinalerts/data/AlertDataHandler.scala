package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.{ApplicationResult, FutureApplicationResult}
import com.alexitc.coinalerts.models._

trait AlertDataHandler[F[_]] {

  def create(createAlertModel: CreateAlertModel, userId: UserId): F[Alert]

  def markAsTriggered(alertId: AlertId): F[Unit]
  
  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): F[List[Alert]]
}

trait AlertBlockingDataHandler extends AlertDataHandler[ApplicationResult]
