package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.errors.{InvalidBasePriceError, InvalidPriceError}
import com.alexitc.coinalerts.models.{Book, CreateFixedPriceAlertModel, Market}
import org.scalactic.Bad
import org.scalatest.{MustMatchers, WordSpec}

class FixedPriceAlertValidatorSpec extends WordSpec with MustMatchers {

  val validator = new FixedPriceAlertValidator
  val validAlert = CreateFixedPriceAlertModel(Market.BITSO, Book("BTC", "MXN"), true, BigDecimal("4000.00"), None)
  val validAlertWithBasePrice = validAlert.copy(basePrice = Some(BigDecimal("3000.00")))

  "AlertValidator" should {
    "allow a valid alert" in {
      val result = validator.validateCreateModel(validAlert)
      result.isGood mustEqual true
    }

    "allow a valid alert with basePrice" in {
      val result = validator.validateCreateModel(validAlertWithBasePrice)
      result.isGood mustEqual true
    }

    "reject an alert having price <= 0" in {
      val alert = validAlert.copy(price = BigDecimal("0"))
      val result = validator.validateCreateModel(alert)
      result mustEqual Bad(InvalidPriceError).accumulating
    }

    "reject an alert having basePrice <= 0" in {
      val alert = validAlertWithBasePrice.copy(basePrice = Some(BigDecimal("0")))
      val result = validator.validateCreateModel(alert)
      result mustEqual Bad(InvalidBasePriceError).accumulating
    }
  }
}
