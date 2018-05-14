package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{GenericPublicError, InputValidationError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait ReCaptchaError

case object ReCaptchaValidationError extends ReCaptchaError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    val message = messagesApi("error.recaptcha")
    val error = GenericPublicError(message)
    List(error)
  }
}
