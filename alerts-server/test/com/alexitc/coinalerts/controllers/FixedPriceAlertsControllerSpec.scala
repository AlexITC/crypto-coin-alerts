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

class FixedPriceAlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  implicit val userDataHandler: UserBlockingDataHandler = new UserInMemoryDataHandler {}
  implicit val alertDataHandler: FixedPriceAlertBlockingDataHandler = new FixedPriceAlertInMemoryDataHandler {}

  val application: Application = guiceApplicationBuilder
      .overrides(bind[UserBlockingDataHandler].to(userDataHandler))
      .overrides(bind[FixedPriceAlertBlockingDataHandler].to(alertDataHandler))
      .build()

  val jwtService = application.injector.instanceOf[JWTService]

  "POST /fixed-price-alerts" should {
    val url = "/fixed-price-alerts"

    "Create an alert" in {
      val body =
        """
          | {
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").asOpt[Long].isDefined mustEqual true
      (json \ "market").as[String] mustEqual "BITTREX"
      (json \ "book").as[String] mustEqual "BTC_ETH"
      (json \ "isGreaterThan").as[Boolean] mustEqual false
      (json \ "price").asOpt[BigDecimal].isDefined mustEqual true
      (json \ "basePrice").asOpt[BigDecimal].isDefined mustEqual false
    }

    "Create an alert with basePrice" in {
      val body =
        """
          | {
          |   "market": "BITSO",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": true,
          |   "price": "0.123456789",
          |   "basePrice": "0.1234"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").asOpt[Long].isDefined mustEqual true
      (json \ "market").as[String] mustEqual "BITSO"
      (json \ "book").as[String] mustEqual "BTC_ETH"
      (json \ "isGreaterThan").as[Boolean] mustEqual true
      (json \ "price").asOpt[BigDecimal].isDefined mustEqual true
      (json \ "basePrice").asOpt[BigDecimal].isDefined mustEqual true
    }

    "Fail to create an alert with an invalid market" in {
      val body =
        """
          | {
          |   "market": "UNKNOWN",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "market"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "Fail to create an alert with an incorrect book format" in {
      val body =
        """
          | {
          |   "market": "BITTREX",
          |   "book": "BTC.ETH",
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "book"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "Fail to create an alert having price less than 0" in {
      val body =
        """
          | {
          |   "market": "BITTREX",
          |   "book": "BTC_ETH",
          |   "isGreaterThan": false,
          |   "price": "-0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "price"
      (error \ "message").as[String].nonEmpty mustEqual true
    }
  }

  "GET /fixed-price-alerts" should {
    val url = "/fixed-price-alerts"

    "Return a paginated result based on the query" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user.id)
      createFixedPriceAlert(user.id)
      createFixedPriceAlert(user.id)

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
