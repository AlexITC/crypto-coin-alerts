package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.clients.BitsoClient
import com.alexitc.coinalerts.tasks.TickerCollector
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class BitsoTickerCollector @Inject() (bitsoClient: BitsoClient) extends TickerCollector {

  override val market: Market = Market.BITSO

  override def getTickerList: Future[List[Ticker]] = {
    bitsoClient.getTickerList
  }
}
