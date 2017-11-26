package com.alexitc.coinalerts.core

import play.api.libs.json.{JsNumber, Writes}

case class Limit(int: Int) extends AnyVal
object Limit {

  implicit val writes: Writes[Limit] = Writes[Limit] { limit => JsNumber(limit.int) }
}
