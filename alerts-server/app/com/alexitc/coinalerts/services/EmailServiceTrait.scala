package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.models.UserEmail
import com.alexitc.coinalerts.services.EmailMessagesProvider.{EmailSubject, EmailText}
import com.alexitc.playsonify.core.FutureApplicationResult

trait EmailServiceTrait {

  def sendEmail(destination: UserEmail, subject: EmailSubject, text: EmailText): FutureApplicationResult[Unit]
}
