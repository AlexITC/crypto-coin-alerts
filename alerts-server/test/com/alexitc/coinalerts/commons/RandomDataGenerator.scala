package com.alexitc.coinalerts.commons

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

  def email = {
    val user = alpha(8)
    val domain = alpha(5)
    UserEmail(s"$user@$domain.com")
  }

  def password = UserPassword(alphanumeric(12))

  def hiddenPassword = UserHiddenPassword.fromPassword(password)

  def alertId = AlertId(Random.nextLong())

  def createDefaultAlertModel(
      market: Market = Market.BITSO,
      book: Book = Book("BTC", "MXN"),
      isGreaterThan: Boolean = Random.nextBoolean(),
      givenPrice: BigDecimal = BigDecimal(Math.abs(Random.nextDouble()))) = CreateAlertModel(AlertType.DEFAULT, market, book, isGreaterThan, givenPrice, None)
}
