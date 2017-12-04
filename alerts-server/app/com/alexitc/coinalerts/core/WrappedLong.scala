package com.alexitc.coinalerts.core

import play.api.libs.json.{JsNumber, Writes}

trait WrappedLong extends Any {
  def long: Long
}

object WrappedLong {

  implicit val writes: Writes[WrappedLong] = {
    Writes[WrappedLong] { wrappedLong => JsNumber(wrappedLong.long) }
  }
}
