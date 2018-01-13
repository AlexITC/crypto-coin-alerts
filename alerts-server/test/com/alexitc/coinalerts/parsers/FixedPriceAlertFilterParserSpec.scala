package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.models.FixedPriceAlertFilter.{AnyTriggeredCondition, HasBeenTriggeredCondition, HasNotBeenTriggeredCondition, JustThisUserCondition}
import com.alexitc.coinalerts.models.UserId
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertFilterParserSpec extends WordSpec with MustMatchers {

  val parser = new FixedPriceAlertFilterParser
  val userId = UserId.create

  "parsing a string" should {
    "allow an empty string" in {
      val filter = ""
      val result = parser.from(filter, userId).get

      result.triggered mustEqual AnyTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=true" in {
      val filter = "triggered=true"
      val result = parser.from(filter, userId).get

      result.triggered mustEqual HasBeenTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=false" in {
      val filter = "triggered=false"
      val result = parser.from(filter, userId).get

      result.triggered mustEqual HasNotBeenTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=*" in {
      val filter = "triggered=*"
      val result = parser.from(filter, userId).get

      result.triggered mustEqual AnyTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "fail on unknown filter" in {
      val filter = "user=*"
      val result = parser.from(filter, userId)

      result.isDefined mustEqual false
    }

    "fail on valid key with no value" in {
      val filter = "triggered="
      val result = parser.from(filter, userId)

      result.isDefined mustEqual false
    }

    "fail on invalid format" in {
      val filter = "triggered,"
      val result = parser.from(filter, userId)

      result.isDefined mustEqual false
    }
  }
}
