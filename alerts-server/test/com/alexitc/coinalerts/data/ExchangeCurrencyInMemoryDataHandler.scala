package com.alexitc.coinalerts.data
import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.RepeatedExchangeCurrencyError
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait ExchangeCurrencyInMemoryDataHandler extends ExchangeCurrencyBlockingDataHandler {

  private val currencyList = new mutable.ListBuffer[ExchangeCurrency]()

  override def create(
      exchange: Exchange,
      market: Market,
      currency: Currency): ApplicationResult[ExchangeCurrency] = currencyList.synchronized {

    if (getBy(exchange, market, currency).get.isDefined) {
      Bad(RepeatedExchangeCurrencyError).accumulating
    } else {
      val newCurrency = ExchangeCurrency(
        ExchangeCurrencyId(currencyList.length),
        exchange,
        market,
        currency)

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
      currency: Currency): ApplicationResult[Option[ExchangeCurrency]] = currencyList.synchronized {

    val result = currencyList.find { c =>
      c.exchange == exchange && c.market == market && c.currency == currency
    }

    Good(result)
  }

  override def getAll(): ApplicationResult[List[ExchangeCurrency]] = currencyList.synchronized {
    Good(currencyList.toList)
  }
}
