package com.alexitc.coinalerts.common

import com.alexitc.coinalerts.models.{AlertId, UserEmail, UserHiddenPassword, UserPassword}

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
}
