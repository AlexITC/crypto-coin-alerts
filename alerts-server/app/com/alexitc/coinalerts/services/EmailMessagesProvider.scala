package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.config.AppConfig
import com.alexitc.coinalerts.models.UserVerificationToken
import play.api.i18n.{Lang, MessagesApi}

class EmailMessagesProvider @Inject() (messagesApi: MessagesApi, appConfig: AppConfig) {

  def verifyEmailSubject(implicit lang: Lang): EmailSubject = {
    val string = messagesApi("email.verificationToken.subject")
    new EmailSubject(string)
  }

  def verifyEmailText(token: UserVerificationToken)(implicit lang: Lang): EmailText = {
    val suffixURL = s"/verify-email/${token.string}"
    val url = appConfig.url.concat(suffixURL)
    val string = messagesApi("email.verificationToken.text", url.string)

    new EmailText(string)
  }

  def yourAlertsSubject(implicit lang: Lang): EmailSubject = {
    val string = messagesApi("email.yourAlerts.subject")
    new EmailSubject(string)
  }

  def yourFixedPriceAlertsText(body: String)(implicit lang: Lang): EmailText = {
    val suffixURL = "/new-fixed-price-alert"
    val url = appConfig.url.concat(suffixURL)
    val footer = messagesApi("email.fixedPriceAlerts.footer", url.string)

    new EmailText(s"$body\n\n\n\n$footer")
  }
}

class EmailSubject(val string: String) extends AnyVal
class EmailText(val string: String) extends AnyVal
