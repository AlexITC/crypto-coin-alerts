package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.models.UserVerificationToken
import play.api.i18n.{Lang, MessagesApi}

class EmailMessagesProvider @Inject() (messagesApi: MessagesApi) {

  def verifyEmailSubject(implicit lang: Lang): EmailSubject = {
    val string = messagesApi("email.verificationToken.subject")
    new EmailSubject(string)
  }

  def verifyEmailText(token: UserVerificationToken)(implicit lang: Lang): EmailText = {
    val string = messagesApi("email.verificationToken.text", token.string)
    new EmailText(string)
  }

  def yourAlertsSubject(implicit lang: Lang): EmailSubject = {
    val string = messagesApi("email.yourAlerts.subject")
    new EmailSubject(string)
  }
}

class EmailSubject(val string: String) extends AnyVal
class EmailText(val string: String) extends AnyVal
