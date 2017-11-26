package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.DataHelper._
import com.alexitc.coinalerts.commons.PlayAPISpec
import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.services.JWTService
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class AlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  implicit val userDataHandler: UserBlockingDataHandler = new UserInMemoryDataHandler {}
  implicit val alertDataHandler: AlertBlockingDataHandler = new AlertInMemoryDataHandler {}

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

  "GET /alerts" should {
    val url = "/alerts"

    "Return a paginated result based on the query" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      createAlert(user.id)
      createAlert(user.id)

      val query = PaginatedQuery(Offset(1), Limit(10))
      val response = GET(url.withQueryParams(query), token.toHeader)
      val json = contentAsJson(response)
      status(response) mustEqual OK
      (json \ "offset").as[Int] mustEqual query.offset.int
      (json \ "limit").as[Int] mustEqual query.limit.int
      (json \ "total").as[Int] mustEqual 2

      val alertJsonList = (json \ "data").as[List[JsValue]]
      alertJsonList.length mustEqual 1
      (json \ "data").as[List[JsValue]].foreach { alertJson =>
        (alertJson \ "id").asOpt[Long].isDefined mustEqual true
      }
    }

    "Allow to not set limit and offset params" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val response = GET(url, token.toHeader)
      status(response) mustEqual OK
    }

    "Fail when offset param is negative" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val response = GET(url.withQueryParams("offset" -> "-1"), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true
      errorList.foreach { jsonError =>
        (jsonError \ "type").as[String] mustEqual "field-validation-error"
        (jsonError \ "field").as[String] mustEqual "offset"
        (jsonError \ "message").as[String].nonEmpty mustEqual true
      }
    }

    "Fail when offset param is not an integer" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val response = GET(url.withQueryParams("offset" -> "whoops"), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true
      errorList.foreach { jsonError =>
        (jsonError \ "type").as[String] mustEqual "generic-error"
        (jsonError \ "message").as[String].nonEmpty mustEqual true
      }
    }

    "Fail when limit param is negative" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val response = GET(url.withQueryParams("limit" -> "-1"), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true
      errorList.foreach { jsonError =>
        (jsonError \ "type").as[String] mustEqual "field-validation-error"
        (jsonError \ "field").as[String] mustEqual "limit"
        (jsonError \ "message").as[String].nonEmpty mustEqual true
      }
    }

    "Fail when limit param is not an integer" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val response = GET(url.withQueryParams("limit" -> "whoops"), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true
      errorList.foreach { jsonError =>
        (jsonError \ "type").as[String] mustEqual "generic-error"
        (jsonError \ "message").as[String].nonEmpty mustEqual true
      }
    }
  }
}
