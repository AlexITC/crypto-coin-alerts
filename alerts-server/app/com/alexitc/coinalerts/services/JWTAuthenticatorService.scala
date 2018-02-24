package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{AbstractAuthenticatorService, ApplicationResult, FutureApplicationResult}
import com.alexitc.coinalerts.core.AuthorizationToken
import com.alexitc.coinalerts.errors.{AuthorizationHeaderRequiredError, InvalidJWTError}
import com.alexitc.coinalerts.models.UserId
import org.scalactic.{Bad, One, Or}
import play.api.http.HeaderNames
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.Future

class JWTAuthenticatorService @Inject() (jwtService: JWTService) extends AbstractAuthenticatorService[UserId] {

  override def authenticate(request: Request[JsValue]): FutureApplicationResult[UserId] = {
    val result = for {
      authorizationHeader <- Or.from(
        request.headers.get(HeaderNames.AUTHORIZATION),
        One(AuthorizationHeaderRequiredError))

      userId <- decodeAuthorizationHeader(authorizationHeader)
    } yield userId

    Future.successful(result)
  }

  private def decodeAuthorizationHeader(header: String): ApplicationResult[UserId] = {
    val tokenType = "Bearer"
    val headerParts = header.split(" ")

    Option(headerParts)
        .filter(_.length == 2)
        .filter(tokenType equalsIgnoreCase _.head)
        .map(_.drop(1).head)
        .map(AuthorizationToken.apply)
        .map { token =>
          jwtService.decodeToken(token).map(_.id)
        }
        .getOrElse(Bad(InvalidJWTError).accumulating)
  }
}
