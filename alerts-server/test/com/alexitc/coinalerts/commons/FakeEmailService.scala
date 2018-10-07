package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.models.UserEmail
import com.alexitc.coinalerts.services.EmailMessagesProvider.{EmailSubject, EmailText}
import com.alexitc.coinalerts.services.EmailServiceTrait
import com.alexitc.playsonify.core.FutureApplicationResult
import org.scalactic.Good
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class FakeEmailService extends EmailServiceTrait {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def sendEmail(
      destination: UserEmail,
      subject: EmailSubject,
      text: EmailText): FutureApplicationResult[Unit] = {
    logger.info(s"Sending email to [${destination.string}], subject = [${subject.string}], content = [${text.string}]")

    Future.successful(Good(()))
  }
}
