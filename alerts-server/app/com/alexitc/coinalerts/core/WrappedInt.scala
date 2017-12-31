package com.alexitc.coinalerts.core

import play.api.libs.json.{JsNumber, Writes}

trait WrappedInt extends Any {
  def int: Int
}

object WrappedInt {

  implicit val writes: Writes[WrappedInt] = {
    Writes[WrappedInt] { wrapped => JsNumber(wrapped.int) }
  }
}
