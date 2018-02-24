package com.alexitc.coinalerts.commons.examples

import com.alexitc.coinalerts.commons.examples.CustomErrorMapper.FailedAuthError
import com.alexitc.coinalerts.commons.{AbstractAuthenticatorService, FutureApplicationResult}
import org.scalactic.{One, Or}
import play.api.libs.json.JsValue
import play.api.mvc.Request
import play.api.test.Helpers.AUTHORIZATION

import scala.concurrent.Future

class CustomAuthenticator extends AbstractAuthenticatorService[CustomUser] {

  override def authenticate(request: Request[JsValue]): FutureApplicationResult[CustomUser] = {
    val header = request
        .headers
        .get(AUTHORIZATION)
        .map(CustomUser.apply)

    val result = Or.from(header, One(FailedAuthError))
    Future.successful(result)
  }
}
