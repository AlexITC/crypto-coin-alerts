package com.alexitc.coinalerts.services.external

import com.alexitc.coinalerts.commons.ExecutionContexts
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

class BittrexServiceSpec extends WordSpec with MustMatchers with ScalaFutures with MockitoSugar {

  val ws = mock[WSClient]
  val ec = ExecutionContexts.externalServiceEC
  val service = new BittrexService(ws)(ec)

  "availableBooks" should {
    val responseBody =
      """
        |{
        |  "success": true,
        |  "message": "",
        |  "result": [
        |    {
        |      "MarketCurrency": "VTC",
        |      "BaseCurrency": "BTC",
        |      "MarketCurrencyLong": "Vertcoin",
        |      "BaseCurrencyLong": "Bitcoin",
        |      "MinTradeSize": 0.58028875,
        |      "MarketName": "BTC-VTC",
        |      "IsActive": true,
        |      "Created": "2014-02-13T00:00:00",
        |      "Notice": null,
        |      "LogoUrl": "https://bittrexblobstorage.blob.core.windows.net/public/1f0317bc-c44b-4ea4-8a89-b9a71f3349c8.png"
        |    }
        |  ]
        |}
      """.stripMargin

    "retrieve available books" in {
      val request = mock[WSRequest]
      val response = mock[WSResponse]
      val json = Json.parse(responseBody)

      when(ws.url(anyString)).thenReturn(request)
      when(response.status).thenReturn(200)
      when(response.json).thenReturn(json)
      when(request.get()).thenReturn(Future.successful(response))

      whenReady(service.availableBooks()) { books =>
        books.size mustEqual 1

        val book = books.head
        book.market.string mustEqual "BTC"
        book.currency.string mustEqual "VTC"
      }
    }
  }

  "getTickerList" should {
    val responseBody =
      """
        |{
        |  "success": true,
        |  "message": "",
        |  "result": [
        |    {
        |      "MarketName": "BTC-VTC",
        |      "High": 0.00041662,
        |      "Low": 0.000378,
        |      "Volume": 353784.81888474,
        |      "Last": 0.00038908,
        |      "BaseVolume": 140.43369248,
        |      "TimeStamp": "2018-01-23T02:38:44.64",
        |      "Bid": 0.00038908,
        |      "Ask": 0.00038997,
        |      "OpenBuyOrders": 914,
        |      "OpenSellOrders": 11929,
        |      "PrevDay": 0.00040511,
        |      "Created": "2014-02-13T00:00:00"
        |    }
        |  ]
        |}
      """.stripMargin

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
        ticket.book.currency.string mustEqual "VTC"
        ticket.currentPrice mustEqual BigDecimal("0.00038908")
      }
    }
  }
}
