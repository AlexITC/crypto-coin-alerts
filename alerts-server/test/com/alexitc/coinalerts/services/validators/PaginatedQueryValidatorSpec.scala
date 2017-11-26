package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.errors.{InvalidQueryLimitError, InvalidQueryOffsetError}
import org.scalactic.{Bad, Good}
import org.scalatest.{MustMatchers, WordSpec}

class PaginatedQueryValidatorSpec extends WordSpec with MustMatchers {

  val validator = new PaginatedQueryValidator

  "PaginatedQueryValidator" should {

    "Accept a query having valid offset and limit" in {
      val query = PaginatedQuery(Offset(0), Limit(100))
      val result = validator.validate(query)
      result mustEqual Good(query)
    }

    "Reject a query having offset less than 0" in {
      val query = PaginatedQuery(Offset(-1), Limit(100))
      val result = validator.validate(query)
      result mustEqual Bad(InvalidQueryOffsetError).accumulating
    }

    "Reject a query having limit less than 1" in {
      val query = PaginatedQuery(Offset(0), Limit(0))
      val result = validator.validate(query)
      result mustEqual Bad(InvalidQueryLimitError(100)).accumulating
    }

    "Reject a query having limit greater than 100" in {
      val query = PaginatedQuery(Offset(0), Limit(101))
      val result = validator.validate(query)
      result mustEqual Bad(InvalidQueryLimitError(100)).accumulating
    }
  }
}
