package com.alexitc.coinalerts.models

import org.scalatest.{MustMatchers, WordSpec}

class BookSpec extends WordSpec with MustMatchers {

  "Creating a BITSO book" should {
    "reverse the values to match our format" in {
      val bookString = "LTC_BTC"
      val expectedBookString = "BTC_LTC"

      val actualBook = Book.fromBitsoString(bookString).get

      actualBook.string mustEqual expectedBookString
    }

    "allow creating a book where the currency has 1 character" in {
      val bookString = "R_BTC"
      val expectedBookString = "BTC_R"

      val actualBook = Book.fromBitsoString(bookString).get

      actualBook.string mustEqual expectedBookString
    }
  }
}
