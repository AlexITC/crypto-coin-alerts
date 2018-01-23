package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.services.external.KucoinService
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class KucoinTickerCollector @Inject() (kucoinService: KucoinService) extends TickerCollector {

  override val exchange: Exchange = Exchange.KUCOIN

  override def getTickerList: Future[List[Ticker]] = {
    kucoinService.getTickerList()
  }
}
