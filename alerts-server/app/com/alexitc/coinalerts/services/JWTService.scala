package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.InvalidJWTError
import com.alexitc.coinalerts.models.{AuthorizationToken, UserId}
import org.scalactic.{Bad, Good}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.Json

class JWTService {

  // TODO: Load from config
  val SecretKey = "changeMe"

  def createToken(userId: UserId): AuthorizationToken = {
    val json = s"""{ "id": "${userId.string}" }"""
    val expiresInSeconds = 30L * 24 * 60 * 60 // 30 days
    val claim = JwtClaim(json)
        .issuedNow
        .expiresIn(expiresInSeconds)

    val token = Jwt.encode(claim, SecretKey, JwtAlgorithm.HS384)
    AuthorizationToken(token)
  }

  def decodeToken(token: AuthorizationToken): ApplicationResult[UserId] = {
    Jwt.decode(token.string, SecretKey, Seq(JwtAlgorithm.HS384))
        .map { decodedClaim =>
          val id = (Json.parse(decodedClaim) \ "id").as[String]
          Good(UserId(id))
        }
        .getOrElse {
          Bad(InvalidJWTError).accumulating
        }
  }
}
