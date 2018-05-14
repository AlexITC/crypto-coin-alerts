package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{FieldValidationError, InputValidationError, NotFoundError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait ExchangeCurrencyError

case object UnknownExchangeCurrencyIdError extends ExchangeCurrencyError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.exchangeCurrencyId.unknown")
    val error = FieldValidationError("exchangeCurrencyId", message)
    List(error)
  }
}

case object RepeatedExchangeCurrencyError extends ExchangeCurrencyError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.exchangeCurrency.repeated")
    val error = FieldValidationError("currency", message)
    List(error)
  }
}
case object ExchangeCurrencyNotFoundError extends ExchangeCurrencyError with NotFoundError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.exchangeCurrency.notFound")
    val error = FieldValidationError("currency", message)
    List(error)
  }
}
