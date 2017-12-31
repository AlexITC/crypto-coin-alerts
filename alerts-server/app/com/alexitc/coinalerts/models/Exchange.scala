package com.alexitc.coinalerts.models

import play.api.libs.json._

sealed abstract class Exchange(val string: String)
object Exchange {

  case object BITTREX extends Exchange("BITTREX")
  case object BITSO extends Exchange("BITSO")
  case class UNKNOWN(override val string: String) extends Exchange(string)

  private val fromStringPF: PartialFunction[String, Exchange] = {
    case BITTREX.string => BITTREX
    case BITSO.string => BITSO
  }

  def fromDatabaseString(string: String): Exchange = {
    if (fromStringPF.isDefinedAt(string))
      fromStringPF(string)
    else
      UNKNOWN(string)
  }

  implicit val reads: Reads[Exchange] = {
    JsPath.read[String].collect(JsonValidationError("error.market.unknown"))(fromStringPF)
  }

  implicit val writes: Writes[Exchange] = Writes[Exchange] { market => JsString(market.string) }
}
