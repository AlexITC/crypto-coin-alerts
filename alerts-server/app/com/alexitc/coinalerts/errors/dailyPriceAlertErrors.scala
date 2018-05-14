package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{ConflictError, FieldValidationError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait DailyPriceAlertError

case object RepeatedDailyPriceAlertError extends DailyPriceAlertError with ConflictError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.createDailyPriceAlert.repeated")
    val error = FieldValidationError("exchangeCurrencyId", message)
    List(error)
  }
}
