package com.alexitc.coinalerts.models

import org.scalatest.{MustMatchers, WordSpec}

class BookSpec extends WordSpec with MustMatchers {

  "Creating a BITSO book" should {
    "reverse the values to match our format" in {
      val bookString = "LTC_BTC"
      val expectedBookString = "BTC_LTC"

      val actualBook = BitsoBook.fromString(bookString).get

      actualBook.string mustEqual expectedBookString
    }
  }
}
