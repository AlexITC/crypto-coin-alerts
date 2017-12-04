package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.{DataHelper, PlayAPISpec}
import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.models.{Book, CreateDailyPriceAlertModel, Market}
import com.alexitc.coinalerts.services.JWTService
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class DailyPriceAlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  implicit val userDataHandler: UserBlockingDataHandler = new UserInMemoryDataHandler {}
  implicit val dailyPriceAlertDataHandler: DailyPriceAlertBlockingDataHandler = new DailyPriceAlertInMemoryDataHandler {}

  val application: Application = guiceApplicationBuilder
      .overrides(bind[UserBlockingDataHandler].to(userDataHandler))
      .overrides(bind[DailyPriceAlertBlockingDataHandler].to(dailyPriceAlertDataHandler))
      .build()

  val jwtService = application.injector.instanceOf[JWTService]

  "POST /daily-price-alerts" should {
    val url = "/daily-price-alerts"

    "be able to create a valid daily price alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user.id)

      val market = Market.BITTREX
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
      val market = Market.BITTREX
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
      val token = jwtService.createToken(user.id)

      val market = Market.BITTREX
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
}
