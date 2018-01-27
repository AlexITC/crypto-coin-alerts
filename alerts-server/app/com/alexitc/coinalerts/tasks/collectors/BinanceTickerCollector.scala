package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.services.external.BinanceService
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class BinanceTickerCollector @Inject() (binanceService: BinanceService) extends TickerCollector {

  override val exchange: Exchange = Exchange.BINANCE

  override def getTickerList: Future[List[Ticker]] = {
    binanceService.getTickerList()
  }
}
