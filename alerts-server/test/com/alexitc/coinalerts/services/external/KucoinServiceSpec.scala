package com.alexitc.coinalerts.services.external

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

class KucoinServiceSpec extends WordSpec with MustMatchers with ScalaFutures with MockitoSugar {

  val ws = mock[WSClient]
  val ec = scala.concurrent.ExecutionContext.global
  val service = new KucoinService(ws)(ec)

  val responseBody =
    """
      |{
      |  "success": true,
      |  "code": "OK",
      |  "msg": "Operation succeeded.",
      |  "timestamp": 1516667988276,
      |  "data": [
      |    {
      |      "coinType": "DASH",
      |      "trading": true,
      |      "symbol": "DASH-BTC",
      |      "lastDealPrice":	0.07122997,
      |      "buy": 0.07045,
      |      "sell": 0.0715,
      |      "change": 0.00168996,
      |      "coinTypePair": "BTC",
      |      "sort": 0,
      |      "feeRate": 0.001,
      |      "volValue": 10.94030444,
      |      "high": 0.07236,
      |      "datetime": 1516667986000,
      |      "vol": 154.98382198,
      |      "low": 0.069079,
      |      "changeRate": 0.0243
      |    }
      |  ]
      |}
    """.stripMargin

  "availableBooks" should {
    "retrieve available books" in {
      val request = mock[WSRequest]
      val response = mock[WSResponse]
      val json = Json.parse(responseBody)

      when(ws.url(anyString)).thenReturn(request)
      when(response.status).thenReturn(200)
      when(response.json).thenReturn(json)
      when(request.get()).thenReturn(Future.successful(response))

      whenReady(service.availableBooks) { books =>
        books.size mustEqual 1

        val book = books.head
        book.market.string mustEqual "BTC"
        book.currency.string mustEqual "DASH"
      }
    }
  }

  "getTickerList" should {
    "retrieve the ticket" in {
      val request = mock[WSRequest]
      val response = mock[WSResponse]
      val json = Json.parse(responseBody)

      when(ws.url(anyString)).thenReturn(request)
      when(response.status).thenReturn(200)
      when(response.json).thenReturn(json)
      when(request.get()).thenReturn(Future.successful(response))

      whenReady(service.getTickerList()) { tickerList =>
        tickerList.size mustEqual 1

        val ticket = tickerList.head
        ticket.book.market.string mustEqual "BTC"
        ticket.book.currency.string mustEqual "DASH"
        ticket.currentPrice mustEqual BigDecimal("0.07122997")
      }
    }
  }
}
