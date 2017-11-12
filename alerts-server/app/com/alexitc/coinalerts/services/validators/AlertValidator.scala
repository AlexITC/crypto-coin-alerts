package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{BasePriceNotExpectedError, BasePriceRequiredError, InvalidBasePriceError, InvalidPriceError}
import com.alexitc.coinalerts.models.{AlertType, CreateAlertModel}
import org.scalactic.{Accumulation, Bad, Good}

class AlertValidator {

  def validateCreateAlertModel(createAlertModel: CreateAlertModel): ApplicationResult[CreateAlertModel] = {
    Accumulation.withGood(
      validatePrice(createAlertModel.price),
      validateBasePrice(createAlertModel.basePrice, createAlertModel.alertType)) { (_, _) =>

      createAlertModel
    }
  }

  private def validatePrice(price: BigDecimal): ApplicationResult[BigDecimal] = {
    if (price > 0) {
      Good(price)
    } else {
      Bad(InvalidPriceError).accumulating
    }
  }

  private def validateBasePrice(basePriceMaybe: Option[BigDecimal], alertType: AlertType): ApplicationResult[Option[BigDecimal]] = {
    (alertType, basePriceMaybe) match {
      case (AlertType.BASE_PRICE, Some(basePrice)) =>
        if (basePrice > 0) Good(basePriceMaybe)
        else Bad(InvalidBasePriceError).accumulating

      case (AlertType.BASE_PRICE, None) =>
        Bad(BasePriceRequiredError).accumulating

      case (AlertType.DEFAULT, Some(_)) =>
        Bad(BasePriceNotExpectedError).accumulating

      case _ =>
        Good(basePriceMaybe)
    }
  }
}
