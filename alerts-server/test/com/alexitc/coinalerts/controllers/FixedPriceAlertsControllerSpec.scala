package com.alexitc.coinalerts.controllers

import java.time.OffsetDateTime

import com.alexitc.coinalerts.commons.DataHelper._
import com.alexitc.coinalerts.commons.{PlayAPISpec, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.data.{FixedPriceAlertBlockingDataHandler, FixedPriceAlertInMemoryDataHandler}
import com.alexitc.coinalerts.models.FixedPriceAlertId
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class FixedPriceAlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  implicit val alertDataHandler: FixedPriceAlertBlockingDataHandler = new FixedPriceAlertInMemoryDataHandler {
    override def exchangeCurrencyBlocingDataHandler = Some(exchangeCurrencyDataHandler)
  }

  val application: Application = guiceApplicationBuilder
      .overrides(bind[FixedPriceAlertBlockingDataHandler].to(alertDataHandler))
      .build()

  "POST /fixed-price-alerts" should {
    val url = "/fixed-price-alerts"

    "Create an alert" in {
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val exchangeCurrency = RandomDataGenerator.item(currencies)
      val body =
        s"""
          | {
          |   "exchangeCurrencyId": ${exchangeCurrency.id.int},
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").asOpt[Long].isDefined mustEqual true
      (json \ "exchangeCurrencyId").as[Int] mustEqual exchangeCurrency.id.int
      (json \ "isGreaterThan").as[Boolean] mustEqual false
      (json \ "price").asOpt[BigDecimal].isDefined mustEqual true
      (json \ "basePrice").asOpt[BigDecimal].isDefined mustEqual false
      (json \ "createdOn").asOpt[OffsetDateTime].isDefined mustEqual true
    }

    "Create an alert with basePrice" in {
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val exchangeCurrency = RandomDataGenerator.item(currencies)
      val body =
        s"""
          | {
          |   "exchangeCurrencyId": ${exchangeCurrency.id.int},
          |   "isGreaterThan": true,
          |   "price": "0.123456789",
          |   "basePrice": "0.1234"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").asOpt[Long].isDefined mustEqual true
      (json \ "exchangeCurrencyId").as[Int] mustEqual exchangeCurrency.id.int
      (json \ "isGreaterThan").as[Boolean] mustEqual true
      (json \ "price").asOpt[BigDecimal].isDefined mustEqual true
      (json \ "basePrice").asOpt[BigDecimal].isDefined mustEqual true
    }

    "Fail to create an alert with an unknown currency id" in {
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val missingCurrencyIdInt = currencies.map(_.id.int).max + 1
      val body =
        s"""
          | {
          |   "exchangeCurrencyId": $missingCurrencyIdInt,
          |   "isGreaterThan": false,
          |   "price": "0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "exchangeCurrencyId"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "Fail to create an alert having price less than 0" in {
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val exchangeCurrency = RandomDataGenerator.item(currencies)
      val body =
        s"""
          | {
          |   "exchangeCurrencyId": ${exchangeCurrency.id.int},
          |   "isGreaterThan": false,
          |   "price": "-0.123456789"
          | }
        """.stripMargin

      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
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
      val token = jwtService.createToken(user)
      val currencies = exchangeCurrencyDataHandler.getAll().get
      createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id)
      createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id)

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
      val token = jwtService.createToken(user)

      val response = GET(url, token.toHeader)
      status(response) mustEqual OK
    }

    "Fail when offset param is negative" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)

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
      val token = jwtService.createToken(user)

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
      val token = jwtService.createToken(user)

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
      val token = jwtService.createToken(user)

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

    "Allow to get non-triggered alerts" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val triggeredAlert = createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id).get
      val nonTriggeredAlert = createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id).get

      alertDataHandler.markAsTriggered(triggeredAlert.id)

      val response = GET(url.withQueryParams("filter" -> "triggered:false"), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      val alertJsonList = (json \ "data").as[List[JsValue]]

      alertJsonList.length mustEqual 1
      (alertJsonList.head \ "id").as[Long] mustEqual nonTriggeredAlert.id.long
    }

    "Allow to get triggered alerts" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val triggeredAlert = createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id).get
      val nonTriggeredAlert = createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id).get

      alertDataHandler.markAsTriggered(triggeredAlert.id)

      val response = GET(url.withQueryParams("filter" -> "triggered:true"), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      val alertJsonList = (json \ "data").as[List[JsValue]]

      alertJsonList.length mustEqual 1
      (alertJsonList.head \ "id").as[Long] mustEqual triggeredAlert.id.long
    }

    "Fail when the filter contains invalid keys" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)

      val response = GET(url.withQueryParams("filter" -> "triggered:*,user:*"), token.toHeader)
      status(response) mustEqual BAD_REQUEST

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val jsonError = errorList.head
      (jsonError \ "type").as[String] mustEqual "field-validation-error"
      (jsonError \ "field").as[String] mustEqual "filter"
      (jsonError \ "message").as[String].nonEmpty mustEqual true
    }

    "Allow to get alerts sorted by currency in descending order" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val currencies = exchangeCurrencyDataHandler.getAll().get.sortBy(_.currency.string)
      val size = 6

      RandomDataGenerator.uniqueItems(currencies.map(_.id), size).foreach { exchangeCurrencyId =>
        createFixedPriceAlert(user.id, exchangeCurrencyId)
      }

      val response = GET(url.withQueryParams("orderBy" -> "currency:desc"), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      val alertJsonList = (json \ "data").as[List[JsValue]]
      alertJsonList.size mustEqual size

      val result = alertJsonList.map { json =>
        (json \ "currency").as[String]
      }

      result mustEqual result.sorted.reverse
    }
  }

  "DELETE /fixed-price-alerts/:id" should {
    def url(id: FixedPriceAlertId) = s"/fixed-price-alerts/${id.long}"

    "delete an alert" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val alert = createFixedPriceAlert(user.id, RandomDataGenerator.item(currencies).id).get

      val response = DELETE(url(alert.id), token.toHeader)
      val json = contentAsJson(response)
      status(response) mustEqual OK

      (json \ "id").as[Long] mustEqual alert.id.long
      (json \ "exchangeCurrencyId").as[Int] mustEqual alert.exchangeCurrencyId.int
      (json \ "isGreaterThan").as[Boolean] mustEqual alert.isGreaterThan
      (json \ "price").as[BigDecimal] mustEqual alert.price
      (json \ "basePrice").asOpt[BigDecimal] mustEqual alert.basePrice
    }

    "fail to delete a non existent alert for the current user" in {
      val user = createVerifiedUser()
      val token = jwtService.createToken(user)

      val response = DELETE(url(FixedPriceAlertId(0)), token.toHeader)
      val json = contentAsJson(response)
      status(response) mustEqual NOT_FOUND

      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.length mustEqual 1

      val jsonError = errorList.head
      (jsonError \ "type").as[String] mustEqual "field-validation-error"
      (jsonError \ "field").as[String] mustEqual "fixedPriceAlertId"
      (jsonError \ "message").as[String].nonEmpty mustEqual true
    }
  }
}
