package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.config._
import com.google.inject.AbstractModule

class ConfigModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[FixedPriceAlertConfig]).to(classOf[PlayFixedPriceAlertConfig])
    bind(classOf[FixedPriceAlertsTaskConfig]).to(classOf[PlayFixedPriceAlertsTaskConfig])
    bind(classOf[ExchangeCurrencySeederTaskConfig]).to(classOf[PlayExchangeCurrencySeederTaskConfig])
    bind(classOf[AppConfig]).to(classOf[PlayAppConfig])
    bind(classOf[JWTConfig]).to(classOf[PlayJWTConfig])
    bind(classOf[MailgunConfig]).to(classOf[PlayMailgunConfig])
  }
}
