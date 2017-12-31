package com.alexitc.coinalerts.core

import play.api.libs.json.{JsString, Writes}

trait WrappedString extends Any {
  def string: String
}

object WrappedString {

  implicit val writes: Writes[WrappedString] = {
    Writes[WrappedString] { wrapped => JsString(wrapped.string) }
  }
}
