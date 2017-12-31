package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.UnknownBookError
import com.alexitc.coinalerts.models.{Book, CreateDailyPriceAlertModel, Exchange}
import org.scalactic.{Bad, Good}
import org.scalatest.{MustMatchers, WordSpec}

class DailyPriceAlertValidatorSpec extends WordSpec with MustMatchers {
  val anyMarketBookValidator = new MarketBookValidator {
    override def validate(book: Book, market: Exchange): ApplicationResult[Book] = Good(book)
  }

  val validator = new DailyPriceAlertValidator(anyMarketBookValidator)
  val createModel = CreateDailyPriceAlertModel(Exchange.BITSO, Book("BTC", "ETC"))

  "DailyPriceAlertValidator" should {
    "allow a valid alert" in {
      val result = validator.validate(createModel)
      result mustEqual Good(createModel)
    }

    "reject an alert having an invalid book" in {
      val allInvalidValidator = new MarketBookValidator {
        override def validate(book: Book, market: Exchange): ApplicationResult[Book] = Bad(UnknownBookError).accumulating
      }
      val validator = new DailyPriceAlertValidator(allInvalidValidator)

      val result = validator.validate(createModel)
      result mustEqual Bad(UnknownBookError).accumulating
    }
  }
}
