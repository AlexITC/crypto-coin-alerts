package com.alexitc.coinalerts.models

import java.util.UUID

object RandomIdGenerator {

  def stringId: String = UUID.randomUUID().toString.replace("-", "")
}
