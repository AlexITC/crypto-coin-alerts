package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.errors.InvalidJWTError
import com.alexitc.coinalerts.models.{AuthorizationToken, UserId}
import org.scalactic.{Bad, One}
import org.scalatest.{Matchers, WordSpec}

class JWTServiceSpec extends WordSpec with Matchers {
  
  val jwtService = new JWTService

  "JWTService" should {
    "Allow to encode and decode a user id" in {
      val userId = UserId("my-id")
      val token = jwtService.createToken(userId)
      val decodedUserId = jwtService.decodeToken(token).get

      decodedUserId shouldBe userId
    }

    "Return InvalidJWTError when decoding an invalid JWT" in {
      val token = AuthorizationToken("bad-token")
      jwtService.decodeToken(token) shouldBe Bad(One(InvalidJWTError))
    }
  }
}
