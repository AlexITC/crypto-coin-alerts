package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.data.async.ExchangeCurrencyFutureDataHandler
import com.alexitc.coinalerts.errors.ExchangeCurrencyNotFoundError
import com.alexitc.coinalerts.models.{Exchange, ExchangeCurrency, ExchangeCurrencyId, Market}
import com.alexitc.playsonify.core.FutureApplicationResult
import com.alexitc.playsonify.core.FutureOr.Implicits.FutureOps
import org.scalactic.{Bad, Good}

import scala.concurrent.ExecutionContext

class ExchangeCurrencyService @Inject()(exchangeCurrencyFutureDataHandler: ExchangeCurrencyFutureDataHandler)(
    implicit ec: ExecutionContext) {

  def getCurrency(exchangeCurrencyId: ExchangeCurrencyId): FutureApplicationResult[ExchangeCurrency] = {
    val result = for {
      currency <- exchangeCurrencyFutureDataHandler
        .getBy(exchangeCurrencyId)
        .toFutureOr
        .mapWithError[ExchangeCurrency] {

          case Some(currency) => Good(currency)
          case None => Bad(ExchangeCurrencyNotFoundError).accumulating
        }
    } yield currency

    result.toFuture
  }

  def getCurrencies(exchange: Exchange, market: Market): FutureApplicationResult[List[ExchangeCurrency]] = {
    exchangeCurrencyFutureDataHandler.getBy(exchange, market)
  }

  def getMarkets(exchance: Exchange): FutureApplicationResult[List[Market]] = {
    exchangeCurrencyFutureDataHandler.getMarkets(exchance)
  }
}
