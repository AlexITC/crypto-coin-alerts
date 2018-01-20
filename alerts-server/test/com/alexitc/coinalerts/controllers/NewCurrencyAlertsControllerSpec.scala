package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.{DataHelper, PlayAPISpec}
import com.alexitc.coinalerts.data.{NewCurrencyAlertBlockingDataHandler, NewCurrencyAlertInMemoryDataHandler}
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlertId}
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class NewCurrencyAlertsControllerSpec extends PlayAPISpec {

  import PlayAPISpec._

  val dataHandler = new NewCurrencyAlertInMemoryDataHandler {}

  val application: Application = guiceApplicationBuilder
      .overrides(bind[NewCurrencyAlertBlockingDataHandler].to(dataHandler))
      .build()

  "POST /new-currency-alerts" should {
    val url = "/new-currency-alerts"

    "create a valid alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val exchange = Exchange.BITSO
      val body = s""" { "exchange": "${exchange.string}" } """

      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CREATED

      val json = contentAsJson(response)
      (json \ "id").asOpt[Int].isDefined mustEqual true
      (json \ "exchange").as[String] mustEqual exchange.string
      (json \ "userId").as[String] mustEqual user.id.string
    }

    "reject a repeated alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val exchange = Exchange.BITSO
      val body = s""" { "exchange": "${exchange.string}" } """
      dataHandler.create(user.id, exchange)

      val response = POST(url, Some(body), token.toHeader)
      status(response) mustEqual CONFLICT

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "exchange"
      (error \ "message").as[String].nonEmpty mustEqual true
    }
  }

  "GET /new-currency-alerts" should {
    "retrieve existing alerts" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      dataHandler.create(user.id, Exchange.BITSO)
      dataHandler.create(user.id, Exchange.BITTREX)

      val response = GET("/new-currency-alerts", token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      val list = json.as[List[JsValue]]

      list.size mustEqual 2
      list.exists { item =>
        (item \ "exchange").as[String] == Exchange.BITSO.string
      }
      list.exists { item =>
        (item \ "exchange").as[String] == Exchange.BITTREX.string
      }
    }
  }

  "DELETE /new-currency-alerts/:id" should {
    def url(id: NewCurrencyAlertId) = s"/new-currency-alerts/${id.int}"

    "delete existing alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val exchange = Exchange.BITSO
      val alert = dataHandler.create(user.id, exchange).get

      val response = DELETE(url(alert.id), token.toHeader)
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "id").as[Int] mustEqual alert.id.int
      (json \ "exchange").as[String] mustEqual exchange.string
      (json \ "userId").as[String] mustEqual user.id.string
    }

    "fail to delete non-existent alert" in {
      val user = DataHelper.createVerifiedUser()
      val token = jwtService.createToken(user)
      val alerts = dataHandler.getAll().get
      val nonExistentId = NewCurrencyAlertId(alerts.map(_.id.int).max + 1)

      val response = DELETE(url(nonExistentId), token.toHeader)
      status(response) mustEqual NOT_FOUND

      val json = contentAsJson(response)
      val errorList = (json \ "errors").as[List[JsValue]]
      errorList.nonEmpty mustEqual true

      val error = errorList.head
      (error \ "type").as[String] mustEqual "field-validation-error"
      (error \ "field").as[String] mustEqual "id"
      (error \ "message").as[String].nonEmpty mustEqual true
    }
  }
}
