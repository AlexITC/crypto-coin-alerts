package com.alexitc.coinalerts.services.external

import com.alexitc.coinalerts.commons.ExecutionContexts
import com.alexitc.coinalerts.models.Book
import com.alexitc.coinalerts.tasks.models.Ticker
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

class HitbtcServiceSpec extends WordSpec with MustMatchers with ScalaFutures with MockitoSugar {

  val ws = mock[WSClient]
  val ec = ExecutionContexts.externalServiceEC
  val service = new HitbtcService(ws)(ec)

  val responseBody =
    """
      |[
      |  {
      |    "ask": "0.01587",
      |    "bid": "0.01585",
      |    "last": "0.01588",
      |    "open": "0.01586",
      |    "low": "0.01576",
      |    "high": "0.01612",
      |    "volume": "10087.8",
      |    "volumeQuote": "160.300249",
      |    "timestamp": "2018-01-27T23:50:55.564Z",
      |    "symbol": "LTCBTC"
      |  },
      |  {
      |    "ask": "181.257",
      |    "bid": "181.140",
      |    "last": "181.199",
      |    "open": "175.993",
      |    "low": "172.893",
      |    "high": "183.616",
      |    "volume": "20803.3",
      |    "volumeQuote": "3713376.5629",
      |    "timestamp": "2018-01-27T23:50:55.557Z",
      |    "symbol": "LTCUSD"
      |  },
      |  {
      |    "ask": "0.165",
      |    "bid": "0.163",
      |    "last": "0.165",
      |    "open": "0.169",
      |    "low": "0.159",
      |    "high": "0.171",
      |    "volume": "990.19",
      |    "volumeQuote": "164.00480",
      |    "timestamp": "2018-01-27T23:50:55.465Z",
      |    "symbol": "LTCETH"
      |  },
      |  {
      |    "ask": "1.2152",
      |    "bid": "1.2120",
      |    "last": "1.2130",
      |    "open": "1.2073",
      |    "low": "1.1650",
      |    "high": "1.2391",
      |    "volume": "3483294",
      |    "volumeQuote": "4205623.0490",
      |    "timestamp": "2018-01-27T23:50:55.462Z",
      |    "symbol": "XRPUSDT"
      |  }
      |]
    """.stripMargin

  "availableBooks" should {
    "retrieve available books" in {
      val expectedBooks = "BTC_LTC ETH_LTC USD_LTC USDT_XRP".split(" ").map(Book.fromString).map(_.get).toList

      val request = mock[WSRequest]
      val response = mock[WSResponse]
      val json = Json.parse(responseBody)

      when(ws.url(anyString)).thenReturn(request)
      when(response.status).thenReturn(200)
      when(response.json).thenReturn(json)
      when(request.get()).thenReturn(Future.successful(response))

      whenReady(service.availableBooks()) { books =>
        books.size mustEqual expectedBooks.size

        books.sortBy(_.string) mustEqual expectedBooks.sortBy(_.string)
      }
    }
  }

  "getTickerList" should {
    "retrieve the ticket" in {
      val expectedTickers = List(
        "BTC_LTC" -> BigDecimal("0.01588"),
        "ETH_LTC" -> BigDecimal("0.165"),
        "USD_LTC" -> BigDecimal("181.199"),
        "USDT_XRP" -> BigDecimal("1.2130")
      ).map { case (string, price) => Ticker(Book.fromString(string).get, price) }

      val request = mock[WSRequest]
      val response = mock[WSResponse]
      val json = Json.parse(responseBody)

      when(ws.url(anyString)).thenReturn(request)
      when(response.status).thenReturn(200)
      when(response.json).thenReturn(json)
      when(request.get()).thenReturn(Future.successful(response))

      whenReady(service.getTickerList()) { tickerList =>
        tickerList.size mustEqual expectedTickers.size

        tickerList.sortBy(_.book.string) mustEqual expectedTickers.sortBy(_.book.string)
      }
    }
  }
}
