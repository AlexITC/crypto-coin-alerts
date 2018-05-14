package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{ConflictError, FieldValidationError, NotFoundError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait NewCurrencyAlertError

case object RepeatedExchangeError extends NewCurrencyAlertError with ConflictError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.newCurrencyAlert.repeatedExchange")
    val error = FieldValidationError("exchange", message)
    List(error)
  }
}

case object NewCurrencyAlertNotFoundError extends NewCurrencyAlertError with NotFoundError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.newCurrencyAlert.notFound")
    val error = FieldValidationError("exchange", message)
    List(error)
  }
}
