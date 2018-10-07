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

  /**
   * BITSO represents a book in the reversed order than us
   */
  def fromBitsoString(string: String): Option[Book] = {
    Option(string.toUpperCase.split("_"))
      .filter(_.length == 2)
      .flatMap { parts =>
        for {
          currency <- Currency.from(parts(0))
          market <- Market.from(parts(1))
        } yield Book(market, currency)
      }
  }
}
