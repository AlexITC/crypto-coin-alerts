package com.alexitc.coinalerts.modules

import com.bitso.Bitso
import com.google.inject.AbstractModule

class BitsoModule extends AbstractModule {

  override def configure(): Unit = {
    // we don't use anything required API keys
    val bitso = new Bitso("", "")
    bind(classOf[Bitso]).toInstance(bitso)
  }
}
