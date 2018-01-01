package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._

import scala.language.higherKinds

trait ExchangeCurrencyDataHandler[F[_]] {

  def create(exchange: Exchange, market: Market, currency: Currency): F[Option[ExchangeCurrency]]

  def getBy(exchangeCurrencyId: ExchangeCurrencyId): F[Option[ExchangeCurrency]]

  def getBy(exchange: Exchange, market: Market, currency: Currency): F[Option[ExchangeCurrency]]

  def getAll(): F[List[ExchangeCurrency]]
}

trait ExchangeCurrencyBlockingDataHandler extends ExchangeCurrencyDataHandler[ApplicationResult]
