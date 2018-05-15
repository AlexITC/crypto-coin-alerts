package com.alexitc.coinalerts.models

import org.scalatest.{MustMatchers, OptionValues, WordSpec}

class MarketSpec extends WordSpec with MustMatchers with OptionValues {

  "from" should {
    "Allow two upper case letters" in {
      val input = "RX"
      val result = Market.from(input)
      result.value.string mustEqual input.toUpperCase
    }

    "Allow an alphanumeric string with 10 characters" in {
      val input = "aZqZ121390"
      val result = Market.from(input)
      result.value.string mustEqual input.toUpperCase
    }

    "Allow an alphanumeric lower case string and map it to upper case" in {
      val input = "btc"
      val result = Market.from(input)
      result.value.string mustEqual input.toUpperCase
    }

    "Reject a single upper case letter" in {
      val input = "R"
      val result = Market.from(input)
      result.isEmpty mustEqual true
    }

    "Reject symbols" in {
      val input = "R-C"
      val result = Market.from(input)
      result.isEmpty mustEqual true
    }

    "Reject an alphanumeric string with 11 characters" in {
      val input = "aZqZ121390a"
      val result = Market.from(input)
      result.isEmpty mustEqual true
    }
  }
}
