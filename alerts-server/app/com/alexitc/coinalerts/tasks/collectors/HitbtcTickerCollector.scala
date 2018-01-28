package com.alexitc.coinalerts.tasks.collectors

import javax.inject.Inject

import com.alexitc.coinalerts.models.Exchange
import com.alexitc.coinalerts.services.external.HitbtcService
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

class HitbtcTickerCollector @Inject() (hitbtcService: HitbtcService) extends TickerCollector {

  override val exchange: Exchange = Exchange.HITBTC

  override def getTickerList: Future[List[Ticker]] = {
    hitbtcService.getTickerList()
  }
}

