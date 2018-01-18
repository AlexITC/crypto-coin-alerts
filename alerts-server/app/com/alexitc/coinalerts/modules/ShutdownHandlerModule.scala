package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.core.ShutdownHandler
import com.google.inject.AbstractModule

class ShutdownHandlerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ShutdownHandler]).asEagerSingleton()
  }
}
