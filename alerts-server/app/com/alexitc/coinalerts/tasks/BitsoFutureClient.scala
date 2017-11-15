package com.alexitc.coinalerts.tasks

import javax.inject.Inject

import com.alexitc.coinalerts.config.TaskExecutionContext
import com.bitso.{Bitso, BitsoTicker}

import scala.concurrent.Future

class BitsoFutureClient @Inject() (bitso: Bitso)(implicit ec: TaskExecutionContext) {

  def getTickerList: Future[List[BitsoTicker]] = Future {
    bitso.getTicker.toList
  }
}
