package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models._
import play.api.i18n.{Lang, MessagesApi}

sealed trait FixedPriceAlertError

case object InvalidPriceError extends FixedPriceAlertError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.price.invalid")
    val error = FieldValidationError("price", message)
    List(error)
  }
}

case object InvalidBasePriceError extends FixedPriceAlertError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.basePrice.invalid")
    val error = FieldValidationError("basePrice", message)
    List(error)
  }
}

case object FixedPriceAlertNotFoundError extends FixedPriceAlertError with NotFoundError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.fixedPriceAlert.notFound")
    val error = FieldValidationError("fixedPriceAlertId", message)
    List(error)
  }
}

case class TooManyFixedPriceAlertsError(reachedLimit: Count) extends FixedPriceAlertError with ConflictError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.fixedPriceAlert.limitReached", reachedLimit.int)
    val error = GenericPublicError(message)
    List(error)
  }
}

case object InvalidFilterError extends FixedPriceAlertError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.fixedPriceAlert.invalidFilters")
    val error = FieldValidationError("filter", message)
    List(error)
  }
}

case object InvalidOrderError extends FixedPriceAlertError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.fixedPriceAlert.invalidOrder")
    val error = FieldValidationError("orderBy", message)
    List(error)
  }
}
