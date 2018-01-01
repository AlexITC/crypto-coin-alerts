package com.alexitc.coinalerts.data
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models._
import org.scalactic.Good

import scala.collection.mutable

trait ExchangeCurrencyInMemoryDataHandler extends ExchangeCurrencyBlockingDataHandler {

  private val currencyList = new mutable.ListBuffer[ExchangeCurrency]()

  override def create(
      exchange: Exchange,
      market: Market,
      currency: Currency): ApplicationResult[Option[ExchangeCurrency]] = {

    if (getBy(exchange, market, currency).get.isDefined) {
      Good(None)
    } else {
      val newCurrency = ExchangeCurrency(
        ExchangeCurrencyId(currencyList.length),
        exchange,
        market,
        currency)

      currencyList += newCurrency

      Good(Some(newCurrency))
    }
  }

  override def getBy(exchangeCurrencyId: ExchangeCurrencyId): ApplicationResult[Option[ExchangeCurrency]] = {
    val result = currencyList.find(_.id == exchangeCurrencyId)
    Good(result)
  }

  override def getBy(
      exchange: Exchange,
      market: Market,
      currency: Currency): ApplicationResult[Option[ExchangeCurrency]] = {

    val result = currencyList.find { c =>
      c.exchange == exchange && c.market == market && c.currency == currency
    }

    Good(result)
  }

  override def getAll(): ApplicationResult[List[ExchangeCurrency]] = {
    Good(currencyList.toList)
  }
}
