package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.core.FilterQuery
import com.alexitc.coinalerts.errors.InvalidFilterError
import com.alexitc.coinalerts.models.FixedPriceAlertFilter.{
  AnyTriggeredCondition,
  HasBeenTriggeredCondition,
  HasNotBeenTriggeredCondition,
  JustThisUserCondition
}
import com.alexitc.coinalerts.models.UserId
import org.scalactic.Bad
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertFilterParserSpec extends WordSpec with MustMatchers {

  val parser = new FixedPriceAlertFilterParser
  val userId = UserId.create

  "parsing a string" should {
    "allow an empty string" in {
      val filter = FilterQuery("")
      val result = parser.from(filter, userId).get

      result.triggered mustEqual AnyTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=true" in {
      val filter = FilterQuery("triggered:true")
      val result = parser.from(filter, userId).get

      result.triggered mustEqual HasBeenTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=false" in {
      val filter = FilterQuery("triggered:false")
      val result = parser.from(filter, userId).get

      result.triggered mustEqual HasNotBeenTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }

    "allow triggered=*" in {
      val filter = FilterQuery("triggered:*")
      val result = parser.from(filter, userId).get

      result.triggered mustEqual AnyTriggeredCondition
      result.user mustEqual JustThisUserCondition(userId)
    }
    "fail on triggered= anything other than *, true or false" in {
      val filter = FilterQuery("triggered:xxx")
      val result = parser.from(filter, userId)
      result mustEqual Bad(InvalidFilterError).accumulating
    }

    "fail on unknown filter" in {
      val filter = FilterQuery("user:*")
      val result = parser.from(filter, userId)

      result mustEqual Bad(InvalidFilterError).accumulating
    }

    "fail on valid key with no value" in {
      val filter = FilterQuery("triggered:")
      val result = parser.from(filter, userId)

      result mustEqual Bad(InvalidFilterError).accumulating
    }

    "fail on invalid format" in {
      val filter = FilterQuery("triggered,")
      val result = parser.from(filter, userId)

      result mustEqual Bad(InvalidFilterError).accumulating
    }
  }

  "serializing" should {
    "write out the filter in a 'key=value' format" in {
      val filter = Filter("key", "value")
      filter.toString mustEqual "key=value"
    }
  }
}
