package com.alexitc.coinalerts.services.validators

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.CreateDailyPriceAlertModel

class DailyPriceAlertValidator @Inject() (marketBookValidator: MarketBookValidator) {

  def validate(createModel: CreateDailyPriceAlertModel): ApplicationResult[CreateDailyPriceAlertModel] = {
    marketBookValidator
        .validate(createModel.book, createModel.market)
        .map(_ => createModel)
  }
}
