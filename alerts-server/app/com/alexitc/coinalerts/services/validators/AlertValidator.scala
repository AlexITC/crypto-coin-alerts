package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{InvalidBasePriceError, InvalidPriceError}
import com.alexitc.coinalerts.models.CreateAlertModel
import org.scalactic.{Accumulation, Bad, Good}

class AlertValidator {

  def validateCreateAlertModel(createAlertModel: CreateAlertModel): ApplicationResult[CreateAlertModel] = {
    Accumulation.withGood(
      validatePrice(createAlertModel.price),
      validateBasePrice(createAlertModel.basePrice)) { (_, _) =>

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

  private def validateBasePrice(basePriceMaybe: Option[BigDecimal]): ApplicationResult[Option[BigDecimal]] = basePriceMaybe match {
    case Some(basePrice) if basePrice <= 0 =>
      Bad(InvalidBasePriceError).accumulating

    case _ =>
      Good(basePriceMaybe)
  }
}
