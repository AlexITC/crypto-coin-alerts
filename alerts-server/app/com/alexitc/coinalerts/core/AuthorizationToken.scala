package com.alexitc.coinalerts.core

import play.api.libs.json.{JsObject, JsString, Writes}

case class AuthorizationToken(string: String)
object AuthorizationToken {
  implicit val writes: Writes[AuthorizationToken] = Writes[AuthorizationToken] { token =>
    JsObject(List("token" -> JsString(token.string)))
  }
}
