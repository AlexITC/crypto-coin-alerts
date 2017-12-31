package com.alexitc.coinalerts.tasks.collectors

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

trait TickerCollector {

  def market: Exchange

  def getTickerList: Future[List[Ticker]]

}
