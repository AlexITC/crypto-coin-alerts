package com.alexitc.coinalerts.services.validators

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.UnknownBookError
import com.alexitc.coinalerts.models.{Book, Market}
import org.scalactic.Bad

trait MarketBookValidator  {

  def validate(book: Book, market: Market): ApplicationResult[Book]
}

class MarketBookValidatorImpl @Inject() (
    bitsoBookValidator: BitsoBookValidator,
    bittrexBookValidator: BittrexBookValidator)
    extends MarketBookValidator {

  override def validate(book: Book, market: Market): ApplicationResult[Book] = market match {
    case Market.BITTREX => bitsoBookValidator.validateBook(book)
    case Market.BITSO => bittrexBookValidator.validateBook(book)
    case Market.UNKNOWN(_) => Bad(UnknownBookError).accumulating
  }
}
