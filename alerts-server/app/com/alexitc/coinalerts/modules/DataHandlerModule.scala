package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.data.anorm.{AlertPostgresDataHandler, UserPostgresDataHandler}
import com.alexitc.coinalerts.data.{AlertBlockingDataHandler, UserBlockingDataHandler}
import com.google.inject.AbstractModule

class DataHandlerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UserBlockingDataHandler]).to(classOf[UserPostgresDataHandler])
    bind(classOf[AlertBlockingDataHandler]).to(classOf[AlertPostgresDataHandler])
  }
}
