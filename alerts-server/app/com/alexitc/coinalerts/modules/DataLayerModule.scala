package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.data.AlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.AlertPostgresDataHandler
import com.google.inject.AbstractModule

class DataLayerModule extends AbstractModule{

  override def configure(): Unit = {
    bind(classOf[AlertBlockingDataHandler]).to(classOf[AlertPostgresDataHandler])
  }
}
