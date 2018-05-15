package com.alexitc.coinalerts.models

import com.alexitc.playsonify.models.WrappedString

class Currency private (val string: String) extends AnyVal with WrappedString

object Currency {

  private val pattern = "^[A-Z0-9]{1,10}$"

  def from(string: String): Option[Currency] = {
    Option(string)
        .map(_.toUpperCase)
        .filter(_ matches pattern)
        .map(new Currency(_))
  }
}
