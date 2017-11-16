package com.alexitc.coinalerts.tasks

import com.alexitc.coinalerts.models.Market

import scala.concurrent.Future

trait TickerCollector {

  def market: Market

  def getTickerList: Future[List[Ticker]]

}
