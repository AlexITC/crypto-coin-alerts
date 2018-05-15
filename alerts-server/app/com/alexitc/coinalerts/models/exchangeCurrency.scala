package com.alexitc.coinalerts.models

import com.alexitc.playsonify.models.{WrappedInt, WrappedString}
import play.api.libs.json.{JsPath, Json, Reads, Writes}

/**
 * [[ExchangeCurrency]] represents a currency that can be traded in
 * the [[Market]] supported by the [[Exchange]].
 *
 * For example, I could go to BITTREX (exchange) to buy BTC (market)
 * paying with LTC (currency).
 */
case class ExchangeCurrency(
    id: ExchangeCurrencyId,
    exchange: Exchange,
    market: Market,
    currency: Currency,
    currencyName: Option[CurrencyName])

object ExchangeCurrency {
  implicit val writes: Writes[ExchangeCurrency] = Json.writes[ExchangeCurrency]
}

case class ExchangeCurrencyId(int: Int) extends AnyVal with WrappedInt
object ExchangeCurrencyId {
  implicit val reads: Reads[ExchangeCurrencyId] = {
    JsPath.read[Int].map(ExchangeCurrencyId.apply)
  }
}

case class CurrencyName(string: String) extends AnyVal with WrappedString

case class CreateExchangeCurrencyModel(
    exchange: Exchange,
    market: Market,
    currency: Currency,
    currencyName: Option[CurrencyName])
