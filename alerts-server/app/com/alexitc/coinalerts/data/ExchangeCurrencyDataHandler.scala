package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult

import scala.language.higherKinds

trait ExchangeCurrencyDataHandler[F[_]] {

  def create(exchange: Exchange, market: Market, currency: Currency): F[ExchangeCurrency]

  def getBy(exchangeCurrencyId: ExchangeCurrencyId): F[Option[ExchangeCurrency]]

  def getBy(exchange: Exchange, market: Market, currency: Currency): F[Option[ExchangeCurrency]]

  def getBy(exchange: Exchange, market: Market): F[List[ExchangeCurrency]]

  def getMarkets(exchange: Exchange): F[List[Market]]

  def getAll(): F[List[ExchangeCurrency]]
}

trait ExchangeCurrencyBlockingDataHandler extends ExchangeCurrencyDataHandler[ApplicationResult]
