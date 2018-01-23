package com.alexitc.coinalerts.services.external

import com.alexitc.coinalerts.models.Book
import com.alexitc.coinalerts.tasks.models.Ticker

import scala.concurrent.Future

trait ExchangeService {

  def availableBooks(): Future[List[Book]]

  def getTickerList(): Future[List[Ticker]]

}
