package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.{InputValidationError, PublicError}
import play.api.i18n.{Lang, MessagesApi}

sealed trait MailgunError

case object MailgunSendEmailError extends MailgunError with InputValidationError {

  override def toPublicErrorList(messagesApi: MessagesApi)(implicit lang: Lang): List[PublicError] = {
    List.empty
  }
}
