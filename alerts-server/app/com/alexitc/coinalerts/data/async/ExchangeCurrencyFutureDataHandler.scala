package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.{ExchangeCurrencyBlockingDataHandler, ExchangeCurrencyDataHandler}
import com.alexitc.coinalerts.models.{Currency, Exchange, ExchangeCurrency, Market}

import scala.concurrent.Future

class ExchangeCurrencyFutureDataHandler @Inject() (
    blockingDataHandler: ExchangeCurrencyBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends ExchangeCurrencyDataHandler[FutureApplicationResult] {

  override def getBy(exchange: Exchange, market: Market, currency: Currency): FutureApplicationResult[Option[ExchangeCurrency]] = Future {
    blockingDataHandler.getBy(exchange, market, currency)
  }
}
