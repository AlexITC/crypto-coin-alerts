package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.models.ReCaptchaResponse
import com.alexitc.coinalerts.services.external.ReCaptchaService
import com.alexitc.playsonify.core.FutureApplicationResult
import org.scalactic.Good

import scala.concurrent.Future

class FakeReCaptchaService extends ReCaptchaService {

  override def verify(reCaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit] = {
    Future.successful(Good(()))
  }
}
