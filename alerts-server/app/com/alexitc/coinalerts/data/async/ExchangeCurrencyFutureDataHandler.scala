package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.{ExchangeCurrencyBlockingDataHandler, ExchangeCurrencyDataHandler}
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.FutureApplicationResult

import scala.concurrent.Future

class ExchangeCurrencyFutureDataHandler @Inject() (
    blockingDataHandler: ExchangeCurrencyBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends ExchangeCurrencyDataHandler[FutureApplicationResult] {

  override def create(createModel: CreateExchangeCurrencyModel): FutureApplicationResult[ExchangeCurrency] = Future {
    blockingDataHandler.create(createModel)
  }

  override def getBy(exchangeCurrencyId: ExchangeCurrencyId): FutureApplicationResult[Option[ExchangeCurrency]] = Future {
    blockingDataHandler.getBy(exchangeCurrencyId)
  }

  override def getBy(exchange: Exchange, market: Market, currency: Currency): FutureApplicationResult[Option[ExchangeCurrency]] = Future {
    blockingDataHandler.getBy(exchange, market, currency)
  }

  override def getBy(exchange: Exchange, market: Market): FutureApplicationResult[List[ExchangeCurrency]] = Future {
    blockingDataHandler.getBy(exchange, market)
  }

  override def getMarkets(exchange: Exchange): FutureApplicationResult[List[Market]] = Future {
    blockingDataHandler.getMarkets(exchange)
  }

  override def getAll(): FutureApplicationResult[List[ExchangeCurrency]] = Future {
    blockingDataHandler.getAll()
  }
}
