package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.services.external.{GoogleReCaptchaService, ReCaptchaService}
import com.google.inject.AbstractModule

class ReCaptchaModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ReCaptchaService]).to(classOf[GoogleReCaptchaService])
  }
}
