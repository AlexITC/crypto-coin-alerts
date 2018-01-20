package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.data.anorm._
import com.google.inject.AbstractModule

class DataHandlerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UserBlockingDataHandler]).to(classOf[UserPostgresDataHandler])
    bind(classOf[ExchangeCurrencyBlockingDataHandler]).to(classOf[ExchangeCurrencyPostgresDataHandler])
    bind(classOf[FixedPriceAlertBlockingDataHandler]).to(classOf[FixedPriceAlertPostgresDataHandler])
    bind(classOf[DailyPriceAlertBlockingDataHandler]).to(classOf[DailyPriceAlertPostgresDataHandler])
    bind(classOf[NewCurrencyAlertBlockingDataHandler]).to(classOf[NewCurrencyAlertPostgresDataHandler])
  }
}
