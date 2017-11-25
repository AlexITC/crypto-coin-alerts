package com.alexitc.coinalerts.services

import com.alexitc.coinalerts.config.{JWTConfig, JWTSecretKey}
import com.alexitc.coinalerts.core.AuthorizationToken
import com.alexitc.coinalerts.errors.InvalidJWTError
import com.alexitc.coinalerts.models.UserId
import org.scalactic.{Bad, One}
import org.scalatest.{Matchers, WordSpec}

class JWTServiceSpec extends WordSpec with Matchers {

  def jwtServiceWithKey(secret: String): JWTService = {
    val config = new JWTConfig {
      override def secretKey: JWTSecretKey = JWTSecretKey(secret)
    }

    new JWTService(config)
  }

  "JWTService" should {
    "Allow to encode and decode a user id" in {
      val jwtService = jwtServiceWithKey("secret-key")
      val userId = UserId("my-id")
      val token = jwtService.createToken(userId)
      val decodedUserId = jwtService.decodeToken(token).get

      decodedUserId shouldBe userId
    }

    "Return InvalidJWTError when decoding an invalid JWT" in {
      val jwtService = jwtServiceWithKey("secret-key")
      val token = AuthorizationToken("bad-token")
      jwtService.decodeToken(token) shouldBe Bad(One(InvalidJWTError))
    }

    "Return InvalidJWTError when decoding a key encoded with a different key" in {
      val jwtService1 = jwtServiceWithKey("secret-key")
      val token = jwtService1.createToken(UserId.create)

      val jwtService2 = jwtServiceWithKey("another-key")
      jwtService2.decodeToken(token) shouldBe Bad(One(InvalidJWTError))
    }
  }
}
