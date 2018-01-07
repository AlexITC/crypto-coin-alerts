package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

trait ReCaptchaConfig {

  def secretKey: ReCaptchaSecretKey
}

class ReCaptchaSecretKey(val string: String) extends AnyVal

class PlayReCaptchaSecretKey @Inject() (config: Configuration) extends ReCaptchaConfig {

  override def secretKey: ReCaptchaSecretKey = {
    val string = config.get[String]("recaptcha.secretKey")
    new ReCaptchaSecretKey(string)
  }
}
