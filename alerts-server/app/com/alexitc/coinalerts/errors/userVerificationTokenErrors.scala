package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{ConflictError, FieldValidationError, NotFoundError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait UserVerificationTokenError

case object UserVerificationTokenNotFoundError extends UserVerificationTokenError with NotFoundError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.token.verification")
    val error = FieldValidationError("token", message)
    List(error)
  }
}

case object UserVerificationTokenAlreadyExistsError extends UserVerificationTokenError with ConflictError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    List.empty
  }
}
