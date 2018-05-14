package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{AuthenticationError, HeaderValidationError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait JWTError

case object AuthorizationHeaderRequiredError extends JWTError with AuthenticationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.header.missing", "Authorization")
    val error = HeaderValidationError("Authorization", message)
    List(error)
  }
}

case object InvalidJWTError extends JWTError with AuthenticationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.jwt.invalid")
    val error = HeaderValidationError("Authorization", message)
    List(error)
  }
}
