package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.config.JWTConfig
import com.alexitc.coinalerts.core.AuthorizationToken
import com.alexitc.coinalerts.errors.InvalidJWTError
import com.alexitc.coinalerts.models.{User, UserEmail, UserId}
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Bad, Good}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.Json

class JWTService @Inject()(config: JWTConfig) {

  def createToken(user: User): AuthorizationToken = {
    val json = s"""{ "id": "${user.id.string}", "email": "${user.email.string}" }"""
    val expiresInSeconds = 30L * 24 * 60 * 60 // 30 days
    val claim = JwtClaim(json).issuedNow
      .expiresIn(expiresInSeconds)

    val token = Jwt.encode(claim, config.secretKey.string, JwtAlgorithm.HS384)
    AuthorizationToken(token)
  }

  def decodeToken(token: AuthorizationToken): ApplicationResult[User] = {
    Jwt
      .decode(token.string, config.secretKey.string, Seq(JwtAlgorithm.HS384))
      .map { decodedClaim =>
        val json = Json.parse(decodedClaim)
        val id = (json \ "id").as[String]
        val email = (json \ "email").as[String]
        Good(User(UserId(id), UserEmail(email)))
      }
      .getOrElse {
        Bad(InvalidJWTError).accumulating
      }
  }
}
