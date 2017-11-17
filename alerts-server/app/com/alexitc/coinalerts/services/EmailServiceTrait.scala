package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.models.UserEmail

trait EmailServiceTrait {

  def sendEmail(destination: UserEmail, subject: EmailSubject, text: EmailText): FutureApplicationResult[Unit]
}
