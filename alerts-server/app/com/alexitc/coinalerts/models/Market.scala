package com.alexitc.coinalerts.models

import com.alexitc.playsonify.models.WrappedString

class Market private (val string: String) extends AnyVal with WrappedString

object Market {

  private val pattern = "^[A-Z0-9]{2,10}$"

  val USD: Market = from("USD").get
  val BTC: Market = from("BTC").get

  def from(string: String): Option[Market] = {
    Option(string)
        .map(_.toUpperCase)
        .filter(_ matches pattern)
        .map(new Market(_))
  }
}
