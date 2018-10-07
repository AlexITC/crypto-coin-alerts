package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.services.external.CoinmarketcapService
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class CoinmarketcapTickerCollector @Inject()(coinmarketcapService: CoinmarketcapService) extends TickerCollector {

  override val exchange: Exchange = Exchange.COINMARKETCAP

  override def getTickerList: Future[List[Ticker]] = {
    coinmarketcapService.getTickerList()
  }
}
