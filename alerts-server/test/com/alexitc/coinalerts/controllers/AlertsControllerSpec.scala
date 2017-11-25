package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.DataHelper._
import com.alexitc.coinalerts.commons.PlayAPISpec
import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.services.JWTService
import play.api.Application
import play.api.inject.bind
import play.api.test.Helpers._

class AlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec.AuthorizationTokenExt

  implicit val userDataHandler = new UserInMemoryDataHandler {}
  val alertDataHandler = new AlertInMemoryDataHandler {}

  val application: Application = guiceApplicationBuilder
      .overrides(bind[UserBlockingDataHandler].to(userDataHandler))
      .overrides(bind[AlertBlockingDataHandler].to(alertDataHandler))
      .build()

  val jwtService = application.injector.instanceOf[JWTService]

  "POST /alerts" should {
    val url = "/alerts"

    "Create a DEFAULT alert" in {
      val json =
        """
          | {
          |   "alertType": "DEFAULT",
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual CREATED
    }

    "Create a BASE_PRICE alert" in {
      val json =
        """
          | {
          |   "alertType": "BASE_PRICE",
          |   "market": "BITSO",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": true,
          |   "price": "0.123456789",
          |   "basePrice": "0.1234"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual CREATED
    }

    "Fail to create an UNKNOWN alert" in {
      val json =
        """
          | {
          |   "alertType": "UNKNOWN",
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to create a DEFAULT alert with UNKNOWN market" in {
      val json =
        """
          | {
          |   "alertType": "DEFAULT",
          |   "market": "UNKNOWN",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to create a DEFAULT alert with an incorrect book format" in {
      val json =
        """
          | {
          |   "alertType": "DEFAULT",
          |   "market": "BITTREX",
          |   "book": "BTC.ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to create a DEFAULT alert having price less than 0" in {
      val json =
        """
          | {
          |   "alertType": "DEFAULT",
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "-0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to create a DEFAULT alert having basePrice" in {
      val json =
        """
          | {
          |   "alertType": "DEFAULT",
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789",
          |   "basePrice": "0.12345"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }

    "Fail to create a BASE_PRICE alert without basePrice" in {
      val json =
        """
          | {
          |   "alertType": "BASE_PRICE",
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(json), token.toHeader)
      status(response) mustEqual BAD_REQUEST
    }
  }
}
