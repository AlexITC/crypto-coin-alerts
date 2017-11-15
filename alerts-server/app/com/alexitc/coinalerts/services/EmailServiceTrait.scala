package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.models.{UserEmail, UserVerificationToken}

trait EmailServiceTrait {

  def sendVerificationToken(email: UserEmail, token: UserVerificationToken): FutureApplicationResult[Unit]

  def sendEmail(destination: UserEmail, subject: String, content: String): FutureApplicationResult[Unit]
}
