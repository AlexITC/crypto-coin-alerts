package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.ReCaptchaConfig
import com.alexitc.coinalerts.errors.ReCaptchaValidationError
import org.scalactic.{Bad, Good}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

trait ReCaptchaService {
  def verify(reCaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit]
}

class GoogleReCaptchaService @Inject() (
    reCaptchaConfig: ReCaptchaConfig,
    ws: WSClient)(
    implicit ec: ExecutionContext)
    extends ReCaptchaService {

  private val url = "https://www.google.com/recaptcha/api/siteverify"

  def verify(recaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit] = {
    val data = Map(
      "secret" -> reCaptchaConfig.secretKey.string,
      "response" -> recaptchaResponse.string
    ).mapValues(List(_))

    ws.url(url).post(data).map { response =>
      (response.json \ "success")
          .asOpt[Boolean]
          .filter(identity)
          .map(_ => Good(()))
          .getOrElse {
            Bad(ReCaptchaValidationError).accumulating
          }
    }
  }
}

class ReCaptchaResponse(val string: String) extends AnyVal
