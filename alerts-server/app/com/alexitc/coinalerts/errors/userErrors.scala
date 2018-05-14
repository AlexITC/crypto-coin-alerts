package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{ConflictError, FieldValidationError, InputValidationError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait UserError

case object InvalidEmailFormatError extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.email.format")
    val error = FieldValidationError("email", message)
    List(error)
  }
}

case class InvalidEmailLengthError(maxLength: Int) extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.email.length", maxLength)
    val error = FieldValidationError("email", message)
    List(error)
  }
}

case class InvalidPasswordLengthError(validLength: Range) extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.password.length", validLength.start, validLength.end)
    val error = FieldValidationError("password", message)
    List(error)
  }
}

case object EmailAlreadyExistsError extends UserError with ConflictError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.email.conflict")
    val error = FieldValidationError("email", message)
    List(error)
  }
}

case object VerifiedUserNotFound extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.verifiedUser.notFound")
    val error = FieldValidationError("email", message)
    List(error)
  }
}

case object IncorrectPasswordError extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.password.incorrect")
    val error = FieldValidationError("password", message)
    List(error)
  }
}

case object UnsupportedLangError extends UserError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.lang.incorrect")
    val error = FieldValidationError("lang", message)
    List(error)
  }
}
