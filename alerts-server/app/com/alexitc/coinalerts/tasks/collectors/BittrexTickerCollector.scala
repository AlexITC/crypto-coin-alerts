package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.tasks.clients.BittrexClient
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class BittrexTickerCollector @Inject() (bittrexClient: BittrexClient) extends TickerCollector {

  override val market: Market = Market.BITTREX

  override def getTickerList: Future[List[Ticker]] = {
    bittrexClient.getTickerList()
  }
}
