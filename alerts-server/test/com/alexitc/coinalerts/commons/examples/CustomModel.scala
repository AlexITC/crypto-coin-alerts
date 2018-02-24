package com.alexitc.coinalerts.commons.examples

import play.api.libs.json.{Format, Json}

case class CustomModel(int: Int, string: String)

object CustomModel {
  implicit val format: Format[CustomModel] = Json.format[CustomModel]
}
