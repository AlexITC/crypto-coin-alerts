package com.alexitc.coinalerts.services.validators

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.CreateDailyPriceAlertModel
import org.scalactic.Good

class DailyPriceAlertValidator {

  def validate(createDailyPriceAlert: CreateDailyPriceAlertModel): ApplicationResult[CreateDailyPriceAlertModel] = {
    // TODO: Validate Book and Price
    Good(createDailyPriceAlert)
  }
}
