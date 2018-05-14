package com.alexitc.coinalerts.models

import com.alexitc.playsonify.models.WrappedString
import play.api.libs.json.{JsPath, Reads}

case class ReCaptchaResponse(string: String) extends AnyVal with WrappedString
object ReCaptchaResponse {
  implicit val reads: Reads[ReCaptchaResponse] = JsPath.read[String].map(ReCaptchaResponse.apply)
}
