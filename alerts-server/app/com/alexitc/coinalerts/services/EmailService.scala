package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.models.{UserEmail, UserVerificationToken}
import com.alexitc.play.tracer.PlayRequestTracing
import org.scalactic.Good

import scala.concurrent.Future

class EmailService extends PlayRequestTracing {

  def sendVerificationToken(email: UserEmail, token: UserVerificationToken): FutureApplicationResult[Unit] = {
    // TODO: Send the email
    logger.info(s"Sending verification token to [${email.string}]")
    Future.successful(Good(()))
  }
}
