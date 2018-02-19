package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.core.ErrorId
import play.api.libs.json.{JsValue, Json}

class PublicErrorRenderer {
  def renderPublicError(publicError: PublicError): JsValue = publicError match {
    case e: GenericPublicError =>
      val obj = Json.obj(
        "type" -> "generic-error",
        "message" -> e.message
      )
      Json.toJson(obj)

    case e: FieldValidationError =>
      val obj = Json.obj(
        "type" -> "field-validation-error",
        "field" -> e.field,
        "message" -> e.message
      )
      Json.toJson(obj)

    case e: HeaderValidationError =>
      val obj = Json.obj(
        "type" -> "header-validation-error",
        "header" -> e.header,
        "message" -> e.message
      )
      Json.toJson(obj)
  }

  def renderPrivateError(errorId: ErrorId) = {
    Json.obj(
      "type" -> "server-error",
      "errorId" -> errorId.string
    )
  }
}
