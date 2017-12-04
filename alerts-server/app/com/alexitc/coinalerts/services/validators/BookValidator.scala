package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.UnknownBookError
import com.alexitc.coinalerts.models.Book
import org.scalactic.{Bad, Good}

/**
 * A [[Book]] is a value dependant of external services like BITSO or BITTREX,
 * then in order to validate it we must provide a custom implementation for
 * every external service.
 *
 * While using dependency injection, we could link service specific implementations
 * using the [[javax.inject.Named]] annotation but it's safer to use custom traits
 * for each service to get the same behavior.
 *
 * We are also interested in providing custom implementations for each service while
 * testing, and that's simple due to having service specific traits.
 */
trait BookValidator {

  protected def availableBooks: List[Book]

  def validateBook(book: Book): ApplicationResult[Book] = {
    availableBooks
        .find(_ == book)
        .map { Good(_) }
        .getOrElse { Bad(UnknownBookError).accumulating }
  }
}

trait BitsoBookValidator extends BookValidator
class BitsoInMemoryBookValidator(protected val availableBooks: List[Book]) extends BitsoBookValidator

trait BittrexBookValidator extends BookValidator
class BittrexInMemoryBookValidator(protected val availableBooks: List[Book]) extends BittrexBookValidator
