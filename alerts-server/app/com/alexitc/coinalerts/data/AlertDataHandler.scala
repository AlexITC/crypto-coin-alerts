package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._

trait AlertDataHandler {

  def create(createAlertModel: CreateAlertModel, userId: UserId): ApplicationResult[Alert]

  def markAsTriggered(alertId: AlertId): ApplicationResult[Unit]
  
  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): ApplicationResult[List[Alert]]
}
