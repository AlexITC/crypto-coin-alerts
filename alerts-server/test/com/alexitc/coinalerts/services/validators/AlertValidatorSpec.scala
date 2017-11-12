package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.errors.{BasePriceNotExpectedError, BasePriceRequiredError, InvalidBasePriceError, InvalidPriceError}
import com.alexitc.coinalerts.models.{AlertType, Book, CreateAlertModel, Market}
import org.scalactic.Bad
import org.scalatest.{MustMatchers, WordSpec}

class AlertValidatorSpec extends WordSpec with MustMatchers {

  val validator = new AlertValidator
  val validDefaultAlert = CreateAlertModel(AlertType.DEFAULT, Market.BITSO, Book("BTC", "MXN"), true, BigDecimal("4000.00"), None)
  val validBasePriceAlert = validDefaultAlert.copy(alertType = AlertType.BASE_PRICE, basePrice = Some(BigDecimal("3000.00")))

  "AlertValidator" should {
    "allow a valid DEFAULT alert" in {
      val result = validator.validateCreateAlertModel(validDefaultAlert)
      result.isGood mustEqual true
    }

    "allow a valid BASE_PRICE alert" in {
      val result = validator.validateCreateAlertModel(validBasePriceAlert)
      result.isGood mustEqual true
    }

    "reject a DEFAULT alert having basePrice" in {
      val alert = validDefaultAlert.copy(basePrice = Some(BigDecimal("20")))
      val result = validator.validateCreateAlertModel(alert)
      result mustEqual Bad(BasePriceNotExpectedError).accumulating
    }

    "reject a DEFAULT alert having price <= 0" in {
      val alert = validDefaultAlert.copy(price = BigDecimal("0"))
      val result = validator.validateCreateAlertModel(alert)
      result mustEqual Bad(InvalidPriceError).accumulating
    }

    "reject a BASE_PRICE alert without basePrice" in {
      val alert = validBasePriceAlert.copy(basePrice = None)
      val result = validator.validateCreateAlertModel(alert)
      result mustEqual Bad(BasePriceRequiredError).accumulating
    }

    "reject a BASE_PRICE alert having basePrice <= 0" in {
      val alert = validBasePriceAlert.copy(basePrice = Some(BigDecimal("0")))
      val result = validator.validateCreateAlertModel(alert)
      result mustEqual Bad(InvalidBasePriceError).accumulating
    }
  }
}
