package com.alexitc.coinalerts.data.anorm.interpreters

import com.alexitc.coinalerts.models.FixedPriceAlertFilter._
import com.alexitc.coinalerts.models.UserId
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertFilterSQLInterpreterSpec extends WordSpec with MustMatchers {

  val interpreter = new FixedPriceAlertFilterSQLInterpreter

  val userId = UserId.create

  "interpreting conditions" should {
    "return empty clause when no condition is required" in {
      val conditions = Conditions(
        triggered = AnyTriggeredCondition,
        user = AnyUserCondition
      )

      val result = interpreter.toWhere(conditions)
      result.sql mustEqual ""
      result.params.isEmpty mustEqual true
    }

    "return where clause when filtering by user" in {
      val conditions = Conditions(
        triggered = AnyTriggeredCondition,
        user = JustThisUserCondition(userId)
      )

      val result = interpreter.toWhere(conditions)
      result.sql mustEqual "WHERE user_id = {user_id}"
      result.params.length mustEqual 1
      result.params.head mustEqual ("user_id" -> userId.string)
    }

    "return where clause when filtering by triggered alerts" in {
      val conditions = Conditions(
        triggered = HasBeenTriggeredCondition,
        user = AnyUserCondition
      )

      val result = interpreter.toWhere(conditions)
      result.sql mustEqual "WHERE triggered_on IS NOT NULL"
      result.params.isEmpty mustEqual true
    }

    "return where clause when filtering by non triggered alerts" in {
      val conditions = Conditions(
        triggered = HasNotBeenTriggeredCondition,
        user = AnyUserCondition
      )

      val result = interpreter.toWhere(conditions)
      result.sql mustEqual "WHERE triggered_on IS NULL"
      result.params.isEmpty mustEqual true
    }
  }
}
