package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.errors.RepeatedExchangeCurrencyError
import com.alexitc.coinalerts.models._
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait ExchangeCurrencyInMemoryDataHandler extends ExchangeCurrencyBlockingDataHandler {

  private val currencyList = new mutable.ListBuffer[ExchangeCurrency]()

  override def create(
      createModel: CreateExchangeCurrencyModel): ApplicationResult[ExchangeCurrency] = currencyList.synchronized {

    if (getBy(createModel.exchange, createModel.market, createModel.currency, createModel.currencyName.getOrElse(CurrencyName(""))).get.isDefined) {
      Bad(RepeatedExchangeCurrencyError).accumulating
    } else {
      val newCurrency = ExchangeCurrency(
        ExchangeCurrencyId(currencyList.length),
        createModel.exchange,
        createModel.market,
        createModel.currency,
        createModel.currencyName)

      currencyList += newCurrency

      Good(newCurrency)
    }
  }

  override def getBy(exchangeCurrencyId: ExchangeCurrencyId): ApplicationResult[Option[ExchangeCurrency]] = currencyList.synchronized {
    val result = currencyList.find(_.id == exchangeCurrencyId)
    Good(result)
  }

  override def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency,
      currencyName: CurrencyName): ApplicationResult[Option[ExchangeCurrency]] = currencyList.synchronized {

    val result = currencyList.find { c =>
      c.exchange == exchange && c.market == market && c.currency == currency && c.currencyName.getOrElse("") == currencyName.string
    }

    Good(result)
  }

  override def getBy(exchange: Exchange, market: Market): ApplicationResult[List[ExchangeCurrency]] = currencyList.synchronized {
    val result = currencyList.toList.filter { c =>
      c.exchange == exchange && c.market == market
    }

    Good(result)
  }

  override def getMarkets(exchange: Exchange): ApplicationResult[List[Market]] = currencyList.synchronized {
    val result = currencyList.toList
        .filter(_.exchange == exchange)
        .map(_.market)

    Good(result)
  }

  override def getAll(): ApplicationResult[List[ExchangeCurrency]] = currencyList.synchronized {
    Good(currencyList.toList)
  }
}
