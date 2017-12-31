package com.alexitc.coinalerts.models

import play.api.libs.json._

case class Book(market: Market, currency: Currency) {
  val string: String = s"${market.string}_${currency.string}".toUpperCase
}
object Book {

  def fromString(string: String): Option[Book] = {
    Option(string.toUpperCase.split("_"))
        .filter(_.length == 2)
        .map { parts =>
          val market = Market(parts(0))
          val currency = Currency(parts(1))
          Book(market, currency)
        }
  }

  implicit val reads: Reads[Book] = {
    JsPath.read[String].collect(JsonValidationError("error.book.invalid")) {
      case string if fromString(string).isDefined => fromString(string).get
    }
  }

  implicit val writes: Writes[Book] = Writes[Book] { book => JsString(book.string) }
}
