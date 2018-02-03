package com.alexitc.coinalerts.models

case class Book(market: Market, currency: Currency, currencyName: Option[CurrencyName] = None) {
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
}

object BitsoBook {

  /**
   * BITSO represents a book in the reversed order than us
   */
  def fromString(string: String): Option[Book] = {
    Book.fromString(string).map { reversedBook =>
      val market = Market(reversedBook.currency.string)
      val currency = Currency(reversedBook.market.string)
      Book(market, currency)
    }
  }
}
