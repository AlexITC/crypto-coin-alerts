package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.ExchangeCurrencyBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.ExchangeCurrencyPostgresDAO
import com.alexitc.coinalerts.models.{Currency, Exchange, ExchangeCurrency, Market}
import org.scalactic.Good
import play.api.db.Database

class ExchangeCurrencyPostgresDataHandler @Inject() (
    exchangeCurrencyDAO: ExchangeCurrencyPostgresDAO,
    protected val database: Database)
    extends ExchangeCurrencyBlockingDataHandler
    with AnormPostgresDAL {

  override def create(
      exchange: Exchange,
      market: Market,
      currency: Currency): ApplicationResult[Option[ExchangeCurrency]] = withConnection { implicit conn =>

    val exchangeCurrencyMaybe = exchangeCurrencyDAO.create(exchange, market, currency)
    Good(exchangeCurrencyMaybe)
  }

  override def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency): ApplicationResult[Option[ExchangeCurrency]] = withConnection { implicit conn =>

    val exchangeCurrencyMaybe = exchangeCurrencyDAO.getBy(exchange, market, currency)
    Good(exchangeCurrencyMaybe)
  }
}
