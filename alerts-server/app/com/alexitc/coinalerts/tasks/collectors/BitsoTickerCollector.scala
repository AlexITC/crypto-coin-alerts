package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.{BitsoFutureClient, Ticker, TickerCollector}

import scala.concurrent.Future

class BitsoTickerCollector @Inject() (bitsoClient: BitsoFutureClient) extends TickerCollector {

  override val market: Market = Market.BITSO

  override def getTickerList: Future[List[Ticker]] = {
    bitsoClient.getTickerList
  }
}
