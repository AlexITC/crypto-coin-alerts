package com.alexitc.coinalerts.services.validators

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.UnknownBookError
import com.alexitc.coinalerts.models.{Book, Exchange}
import org.scalactic.Bad

trait MarketBookValidator  {

  def validate(book: Book, market: Exchange): ApplicationResult[Book]
}

class MarketBookValidatorImpl @Inject() (
    bitsoBookValidator: BitsoBookValidator,
    bittrexBookValidator: BittrexBookValidator)
    extends MarketBookValidator {

  override def validate(book: Book, market: Exchange): ApplicationResult[Book] = market match {
    case Exchange.BITTREX => bitsoBookValidator.validateBook(book)
    case Exchange.BITSO => bittrexBookValidator.validateBook(book)
    case Exchange.UNKNOWN(_) => Bad(UnknownBookError).accumulating
  }
}
