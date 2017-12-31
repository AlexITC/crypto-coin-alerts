package com.alexitc.coinalerts.controllers

import java.time.OffsetDateTime

import com.alexitc.coinalerts.commons.{DataHelper, PlayAPISpec, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.data.{DailyPriceAlertBlockingDataHandler, DailyPriceAlertInMemoryDataHandler}
import com.alexitc.coinalerts.models.{Book, CreateDailyPriceAlertModel, Exchange}
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class DailyPriceAlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  implicit val dailyPriceAlertDataHandler: DailyPriceAlertBlockingDataHandler = new DailyPriceAlertInMemoryDataHandler {}

  val application: Application = guiceApplicationBuilder
      .overrides(bind[DailyPriceAlertBlockingDataHandler].to(dailyPriceAlertDataHandler))
      .build()

  "POST /daily-price-alerts" should {
    val url = "/daily-price-alerts"

    "be able to create a valid daily price alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)

      val market = Exchange.BITTREX
      val book = Book.fromString("BTC_ETH").get
      val body =
        s"""
          |{
          |  "market": "${market.string}",
          |  "book": "${book.string}"
          |}
        """.stripMargin

      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "id").asOpt[Long].isDefined mustEqual true
      (json \ "userId").as[String] mustEqual user.id.string
      (json \ "market").as[String] mustEqual market.string
      (json \ "book").as[String] mustEqual book.string
    }

    "reject a valid daily price alert when no auth token is present" in {
      val market = Exchange.BITTREX
      val book = Book.fromString("BTC_ETH").get
      val body =
        s"""
           |{
           |  "market": "${market.string}",
           |  "book": "${book.string}"
           |}
        """.stripMargin

      val response = POST(url, Some(body))
      status(response) mustEqual UNAUTHORIZED

      val error = (contentAsJson(response) \ "errors").as[List[JsValue]].head
      (error \ "type").as[String] mustEqual "header-validation-error"
      (error \ "header").as[String] mustEqual "Authorization"
      (error \ "message").as[String].nonEmpty mustEqual true
    }

    "reject a repeated alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)

      val market = Exchange.BITTREX
      val book = Book.fromString("BTC_ETH").get
      val body =
        s"""
           |{
           |  "market": "${market.string}",
           |  "book": "${book.string}"
           |}
        """.stripMargin

      dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(market, book))
      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CONFLICT

      val error = (contentAsJson(response) \ "errors").as[List[JsValue]].head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "book"
      (error \ "message").as[String].nonEmpty mustEqual true
    }
  }

  "GET /daily-price-alerts" should {
    val url = "/daily-price-alerts"

    "Return a paginated result based on the query" in {
      val query = PaginatedQuery(Offset(1), Limit(10))
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      DataHelper.createDailyPriceAlert(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("ETH", "MXN")))
      DataHelper.createDailyPriceAlert(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("BTC", "MXN")))

      val response = GET(url.withQueryParams(query), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "total").as[Int] mustEqual 2
      (json \ "offset").as[Int] mustEqual query.offset.int
      (json \ "limit").as[Int] mustEqual query.limit.int

      val jsonList = (contentAsJson(response) \ "data").as[List[JsValue]]
      jsonList.length mustEqual 1

      val jsonItem = jsonList.head
      (jsonItem \ "id").asOpt[Long].isDefined mustEqual true
      (jsonItem \ "createdOn").asOpt[OffsetDateTime].isDefined mustEqual true
      (jsonItem \ "userId").as[String] mustEqual user.id.string
      (jsonItem \ "market").asOpt[String].isDefined mustEqual true
      (jsonItem \ "book").asOpt[String].isDefined mustEqual true
    }
  }
}
