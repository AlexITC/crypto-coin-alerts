package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.config.{ExternalServiceExecutionContext, ReCaptchaConfig}
import com.alexitc.coinalerts.errors.ReCaptchaValidationError
import com.alexitc.coinalerts.models.ReCaptchaResponse
import com.alexitc.playsonify.core.FutureApplicationResult
import org.scalactic.{Bad, Good}
import play.api.libs.ws.WSClient

trait ReCaptchaService {
  def verify(reCaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit]
}

class GoogleReCaptchaService @Inject()(reCaptchaConfig: ReCaptchaConfig, ws: WSClient)(
    implicit ec: ExternalServiceExecutionContext)
    extends ReCaptchaService {

  private val url = "https://www.google.com/recaptcha/api/siteverify"

  def verify(recaptchaResponse: ReCaptchaResponse): FutureApplicationResult[Unit] = {
    val data = Map(
        "secret" -> reCaptchaConfig.secretKey.string,
        "response" -> recaptchaResponse.string
    ).mapValues(List(_))

    // TODO: log invalid key response
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
