package com.alexitc.coinalerts.models

import play.api.libs.json._

sealed abstract class Market(val string: String)
object Market {

  case object BITTREX extends Market("BITTREX")
  case object BITSO extends Market("BITSO")
  case class UNKNOWN(override val string: String) extends Market(string)

  private val fromStringPF: PartialFunction[String, Market] = {
    case BITTREX.string => BITTREX
    case BITSO.string => BITSO
  }

  def fromDatabaseString(string: String): Market = {
    if (fromStringPF.isDefinedAt(string))
      fromStringPF(string)
    else
      UNKNOWN(string)
  }

  implicit val reads: Reads[Market] = {
    JsPath.read[String].collect(JsonValidationError("error.market.unknown"))(fromStringPF)
  }

  implicit val writes: Writes[Market] = Writes[Market] { market => JsString(market.string) }
}
