package com.alexitc.coinalerts.models

import com.alexitc.coinalerts.commons.DataRetrieved
import play.api.libs.json.{JsObject, JsString, Writes}

case class AuthorizationToken(string: String) extends DataRetrieved
object AuthorizationToken {
  implicit val writes: Writes[AuthorizationToken] = Writes[AuthorizationToken] { token =>
    JsObject( List("token" -> JsString(token.string)) )
  }
}
