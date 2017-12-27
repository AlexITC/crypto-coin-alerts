package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.config._
import com.google.inject.AbstractModule

class ConfigModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[AlertTaskConfig]).to(classOf[PlayAlertTaskConfig])
    bind(classOf[AppConfig]).to(classOf[PlayAppConfig])
    bind(classOf[JWTConfig]).to(classOf[PlayJWTConfig])
    bind(classOf[MailgunConfig]).to(classOf[PlayMailgunConfig])
  }
}
