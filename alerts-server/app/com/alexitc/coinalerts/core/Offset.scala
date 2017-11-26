package com.alexitc.coinalerts.core

import play.api.libs.json.{JsNumber, Writes}

case class Offset(int: Int) extends AnyVal
object Offset {

  implicit val writes: Writes[Offset] = Writes[Offset] { offset => JsNumber(offset.int) }
}
