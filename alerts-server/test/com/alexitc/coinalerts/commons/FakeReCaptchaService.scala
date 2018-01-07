package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.services.external.{ReCaptchaResponse, ReCaptchaService}
import org.scalactic.Good

import scala.concurrent.Future

class FakeReCaptchaService extends ReCaptchaService {

  override def verify(reCaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit] = {
    Future.successful(Good(()))
  }
}
