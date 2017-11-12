package com.alexitc.coinalerts.common

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.models.{UserEmail, UserVerificationToken}
import com.alexitc.coinalerts.services.EmailServiceTrait
import org.scalactic.Good
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class FakeEmailService extends EmailServiceTrait {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def sendVerificationToken(email: UserEmail, token: UserVerificationToken): FutureApplicationResult[Unit] = {
    logger.info(s"Sending verification token = [${token.string}], to [${email.string}]")

    Future.successful(Good(()))
  }
}
