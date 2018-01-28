package com.alexitc.coinalerts.models

import play.api.libs.json._

sealed abstract class Exchange(val string: String)
object Exchange {

  case object BITTREX extends Exchange("BITTREX")
  case object BINANCE extends Exchange("BINANCE")
  case object BITSO extends Exchange("BITSO")
  case object HITBTC extends Exchange("HITBTC")
  case object KUCOIN extends Exchange("KUCOIN")
  case class UNKNOWN(override val string: String) extends Exchange(string)

  private val fromStringPF: PartialFunction[String, Exchange] = {
    case BITTREX.string => BITTREX
    case BINANCE.string => BINANCE
    case BITSO.string => BITSO
    case HITBTC.string => HITBTC
    case KUCOIN.string => KUCOIN
  }

  def fromString(string: String): Option[Exchange] = {
    if (fromStringPF.isDefinedAt(string)) {
      Some(fromStringPF(string))
    } else {
      None
    }
  }

  def fromDatabaseString(string: String): Exchange = {
    if (fromStringPF.isDefinedAt(string))
      fromStringPF(string)
    else
      UNKNOWN(string)
  }

  implicit val reads: Reads[Exchange] = {
    JsPath.read[String].collect(JsonValidationError("error.exchange.unknown"))(fromStringPF)
  }

  implicit val writes: Writes[Exchange] = Writes[Exchange] { market => JsString(market.string) }
}
