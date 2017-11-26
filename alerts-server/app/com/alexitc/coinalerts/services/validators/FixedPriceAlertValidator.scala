package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{InvalidBasePriceError, InvalidPriceError}
import com.alexitc.coinalerts.models.CreateFixedPriceAlertModel
import org.scalactic.{Accumulation, Bad, Good}

class FixedPriceAlertValidator {

  def validateCreateModel(createModel: CreateFixedPriceAlertModel): ApplicationResult[CreateFixedPriceAlertModel] = {
    Accumulation.withGood(
      validatePrice(createModel.price),
      validateBasePrice(createModel.basePrice)) { (_, _) =>

      createModel
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
