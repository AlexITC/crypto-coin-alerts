package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.data.ExchangeCurrencyBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.ExchangeCurrencyPostgresDAO
import com.alexitc.coinalerts.errors.RepeatedExchangeCurrencyError
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Good, One, Or}
import play.api.db.Database

class ExchangeCurrencyPostgresDataHandler @Inject() (
    protected val database: Database,
    exchangeCurrencyDAO: ExchangeCurrencyPostgresDAO)
    extends ExchangeCurrencyBlockingDataHandler
    with AnormPostgresDAL {

  override def create(createModel: CreateExchangeCurrencyModel): ApplicationResult[ExchangeCurrency] = withConnection { implicit conn =>

    val exchangeCurrencyMaybe = exchangeCurrencyDAO.create(createModel)

    Or.from(exchangeCurrencyMaybe, One(RepeatedExchangeCurrencyError))
  }

  override def getBy(exchangeCurrencyId: ExchangeCurrencyId): ApplicationResult[Option[ExchangeCurrency]] = withConnection { implicit conn =>
    val exchangeCurrencyMaybe = exchangeCurrencyDAO.getBy(exchangeCurrencyId)
    Good(exchangeCurrencyMaybe)
  }

  override def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency,
      currencyName: CurrencyName): ApplicationResult[Option[ExchangeCurrency]] = withConnection { implicit conn =>

    val exchangeCurrencyMaybe = exchangeCurrencyDAO.getBy(exchange, market, currency, currencyName)
    Good(exchangeCurrencyMaybe)
  }

  override def getBy(
      exchange: Exchange,
      market: Market): ApplicationResult[List[ExchangeCurrency]] = withConnection { implicit conn =>

    val result = exchangeCurrencyDAO.getBy(exchange, market)
    Good(result)
  }

  override def getMarkets(
      exchange: Exchange): ApplicationResult[List[Market]] = withConnection { implicit conn =>

    val result = exchangeCurrencyDAO.getMarkets(exchange)
    Good(result)
  }

  override def getAll(): ApplicationResult[List[ExchangeCurrency]] = withConnection { implicit conn =>
    Good(exchangeCurrencyDAO.getAll)
  }
}
