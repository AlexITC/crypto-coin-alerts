package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.core.OrderByQuery
import com.alexitc.coinalerts.errors.InvalidOrderError
import com.alexitc.coinalerts.models.FixedPriceAlertOrderBy._
import org.scalactic.{Bad, Good}
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertOrderByParserSpec extends WordSpec with MustMatchers {

  val parser = new FixedPriceAlertOrderByParser

  "parsing a string" should {
    "return default conditions for empty string" in {
      val query = OrderByQuery("")

      val result = parser.from(query)
      result mustEqual Good(FixedPriceAlertOrderByParser.DefaultConditions)
    }

    "allow to specify just the field" in {
      val query = OrderByQuery("createdOn")

      val expected = FixedPriceAlertOrderByParser.DefaultConditions.copy(orderBy = OrderByCreatedOn)
      val result = parser.from(query)
      result mustEqual Good(expected)
    }

    "allow to specify the field and ascending order condition" in {
      val query = OrderByQuery("exchange:asc")

      val expected = Conditions(OrderByExchange, AscendingOrderCondition)
      val result = parser.from(query)
      result mustEqual Good(expected)
    }

    "allow to specify the field and descending order condition" in {
      val query = OrderByQuery("currency:desc")

      val expected = Conditions(OrderByCurrency, DescendingOrderCondition)
      val result = parser.from(query)
      result mustEqual Good(expected)
    }

    "fail when there are extra fields" in {
      val query = OrderByQuery("currency:desc:more")

      val result = parser.from(query)
      result mustEqual Bad(InvalidOrderError).accumulating
    }

    "fail when the field is unknown" in {
      val query = OrderByQuery("user:desc")

      val result = parser.from(query)
      result mustEqual Bad(InvalidOrderError).accumulating
    }

    "fail when the order condition is unknown" in {
      val query = OrderByQuery("currency:desceq")

      val result = parser.from(query)
      result mustEqual Bad(InvalidOrderError).accumulating
    }
  }
}
