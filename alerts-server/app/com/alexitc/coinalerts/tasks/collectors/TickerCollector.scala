package com.alexitc.coinalerts.tasks.collectors

import com.alexitc.coinalerts.models.Market
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

trait TickerCollector {

  def market: Market

  def getTickerList: Future[List[Ticker]]

}
