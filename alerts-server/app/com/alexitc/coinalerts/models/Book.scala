package com.alexitc.coinalerts.models

case class Book(market: Market, currency: Currency, currencyName: Option[CurrencyName] = None) {
  val string: String = s"${market.string}_${currency.string}".toUpperCase
}
object Book {

  def fromString(string: String): Option[Book] = {
    Option(string.toUpperCase.split("_"))
        .filter(_.length == 2)
        .flatMap { parts =>
          for {
            market <- Market.from(parts(0))
            currency <- Currency.from(parts(1))
          } yield Book(market, currency)
        }
  }
}

object BitsoBook {

  /**
   * BITSO represents a book in the reversed order than us
   */
  def fromString(string: String): Option[Book] = {
    Book.fromString(string).flatMap { reversedBook =>
      for {
        market <- Market.from(reversedBook.currency.string)
        currency <- Currency.from(reversedBook.market.string)
      } yield Book(market, currency)
    }
  }
}
