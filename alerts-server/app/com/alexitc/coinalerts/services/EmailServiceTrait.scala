package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.models.{UserEmail, UserVerificationToken}

trait EmailServiceTrait {

  def sendVerificationToken(email: UserEmail, token: UserVerificationToken): FutureApplicationResult[Unit]

}
