package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.models.Exchange.{BITSO, BITTREX}
import com.alexitc.coinalerts.models._

import scala.util.Random

object RandomDataGenerator {

  private val AlphaCharset = ('a' to 'z') ++ ('A' to 'Z')
  private val AlphaNumericCharset = AlphaCharset ++ ('0' to '9')

  def alpha(length: Int = 8) = {
    str(AlphaCharset, length)
  }

  def alphanumeric(length: Int = 8) = {
    str(AlphaNumericCharset, length)
  }

  def str(charset: IndexedSeq[Char], length: Int) = {
    (0 until length)
        .map { _ => char(charset) }
        .mkString("")
  }

  def char(charset: IndexedSeq[Char]) = {
    val index = Random.nextInt(charset.length)
    charset(index)
  }

  def item[A](list: Seq[A]): A = {
    val index = Random.nextInt(list.length)
    list(index)
  }

  def uniqueItems[A](list: Seq[A], count: Int): Seq[A] = {
    if (count == 0) List.empty
    else {
      val index = Random.nextInt(list.length)
      val value = list(index)
      val remaining = list.filter(_ != value)
      value :: uniqueItems(remaining, count - 1).toList
    }
  }

  def email = {
    val user = alpha(8)
    val domain = alpha(5)
    UserEmail(s"$user@$domain.com")
  }

  def password = UserPassword(alphanumeric(12))

  def hiddenPassword = UserHiddenPassword.fromPassword(password)

  def alertId = FixedPriceAlertId(Random.nextLong())

  def exchangeCurrencyId = ExchangeCurrencyId(Random.nextInt())

  def dailyPriceAlertId = DailyPriceAlertId(Random.nextLong())

  def exchange = {
    val list = List(BITSO, BITTREX)
    item(list)
  }

  def book = {
    Book(market, currency)
  }

  def market = {
    val marketList = "BTC ETH XRP MXN USD USDT".split(" ")
    Market(item(marketList))
  }

  def currency = {
    val currencyList = "ADA XMR SC RDD LTC BCH LUN DGB XDN MONA THC VTC".split(" ")
    Currency(item(currencyList))
  }

  def exchangeCurrency(id: ExchangeCurrencyId) = {
    ExchangeCurrency(id, exchange, market, currency, None)
  }

  def createFixedPriceAlertModel(
      exchangeCurrencyId: ExchangeCurrencyId,
      isGreaterThan: Boolean = Random.nextBoolean(),
      givenPrice: BigDecimal = BigDecimal(Math.abs(Random.nextDouble()))) = CreateFixedPriceAlertModel(exchangeCurrencyId, isGreaterThan, givenPrice, None)

  def createDailyPriceAlertModel(exchangeCurrencyId: ExchangeCurrencyId) = CreateDailyPriceAlertModel(exchangeCurrencyId)
}
