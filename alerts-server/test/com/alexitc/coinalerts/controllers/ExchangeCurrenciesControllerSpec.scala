package com.alexitc.coinalerts.controllers

import com.alexitc.coinalerts.commons.CustomPlayAPISpec
import com.alexitc.coinalerts.models.{Exchange, ExchangeCurrencyId, Market}
import play.api.Application
import play.api.libs.json.JsValue
import play.api.test.Helpers._

class ExchangeCurrenciesControllerSpec extends CustomPlayAPISpec {

  val application: Application = guiceApplicationBuilder
      .build()

  "GET /currencies/:id" should {
    def url(exchangeCurrencyId: ExchangeCurrencyId) = s"/currencies/${exchangeCurrencyId.int}"

    "retrieve a currency by id" in {
      val exchangeCurrency = exchangeCurrencyDataHandler.getAll().get.head
      val response = GET(url(exchangeCurrency.id))
      status(response) mustEqual OK

      val json = contentAsJson(response)
      (json \ "id").asOpt[Int].isDefined mustEqual true
      (json \ "currency").as[String].nonEmpty mustEqual true
      (json \ "exchange").as[String].nonEmpty mustEqual true
      (json \ "market").as[String].nonEmpty mustEqual true
    }

    "fail to retrieve a non-existent currency" in {
      val id = ExchangeCurrencyId(exchangeCurrencyDataHandler.getAll().get.map(_.id.int).max + 1)
      val response = GET(url(id))
      status(response) mustEqual NOT_FOUND
    }
  }

  "GET /exchanges/:exchange/markets" should {
    def url(exchange: Exchange) = s"/exchanges/${exchange.string}/markets"

    "retrieve the list of markets for an exchange" in {
      val exchange = Exchange.BITSO
      val response = GET(url(exchange))
      status(response) mustEqual OK

      val list = contentAsJson(response).as[List[String]]
      list.nonEmpty mustEqual true
      list.foreach { market =>
        market.nonEmpty mustEqual true
      }
    }

    "fail to retrieve the markets for an unknown exchange" in {
      val response = GET(s"/exchanges/BITFINEX/markets")
      status(response) mustEqual BAD_REQUEST
    }
  }

  "GET /exchanges/:exchange/markets/:market" should {
    def url(exchange: Exchange, market: Market) = s"/exchanges/${exchange.string}/markets/${market.string}/currencies"

    "retrieve the list of currencies for a market" in {
      val exchange = Exchange.BITSO
      val market = Market("BTC")
      val response = GET(url(exchange, market))
      status(response) mustEqual OK

      val list = contentAsJson(response).as[List[JsValue]]
      list.nonEmpty mustEqual true
      list.foreach { json =>
        (json \ "id").asOpt[Int].isDefined mustEqual true
        (json \ "currency").as[String].nonEmpty mustEqual true
        (json \ "exchange").as[String] mustEqual exchange.string
        (json \ "market").as[String] mustEqual market.string
      }
    }

    "retrieve no currencies for an unknown market" in {
      val response = GET(url(Exchange.BITSO, Market("TX")))
      status(response) mustEqual OK

      val list = contentAsJson(response).as[List[JsValue]]
      list.isEmpty mustEqual true
    }
  }
}
