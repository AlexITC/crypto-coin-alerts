package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models._
import com.alexitc.coinalerts.services.external.BittrexService
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class BittrexTickerCollector @Inject() (bittrexService: BittrexService) extends TickerCollector {

  override val market: Market = Market.BITTREX

  override def getTickerList: Future[List[Ticker]] = {
    bittrexService.getTickerList()
  }
}
