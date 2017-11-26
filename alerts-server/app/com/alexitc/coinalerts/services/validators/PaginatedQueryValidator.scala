package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.errors.{InvalidQueryLimitError, InvalidQueryOffsetError}
import org.scalactic.{Accumulation, Bad, Good}

class PaginatedQueryValidator {

  private val MinOffset = 0
  private val LimitRange = 1 to 100

  def validate(query: PaginatedQuery): ApplicationResult[PaginatedQuery] = {
    Accumulation.withGood(
      validateOffset(query.offset),
      validateLimit(query.limit)) {

      PaginatedQuery.apply
    }
  }

  private def validateOffset(offset: Offset): ApplicationResult[Offset] = {
    if (offset.int >= MinOffset) {
      Good(offset)
    } else {
      Bad(InvalidQueryOffsetError).accumulating
    }
  }

  private def validateLimit(limit: Limit): ApplicationResult[Limit] = {
    if (LimitRange contains limit.int) {
      Good(limit)
    } else {
      Bad(InvalidQueryLimitError(LimitRange.last)).accumulating
    }
  }
}
