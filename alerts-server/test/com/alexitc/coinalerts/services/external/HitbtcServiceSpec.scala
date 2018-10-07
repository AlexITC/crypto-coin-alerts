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
      |  },
      |  {
      |    "ask": "19.934",
      |    "bid": "19.828",
      |    "last": "19.777",
      |    "open": "19.868",
      |    "low": "19.777",
      |    "high": "19.777",
      |    "volume": "0.05",
      |    "volumeQuote": "0.98885",
      |    "timestamp": "2018-10-06T14:49:19.400Z",
      |    "symbol": "XMREOS"
      |  },
      |  {
      |    "ask": "57.38",
      |    "bid": "57.00",
      |    "last": "57.25",
      |    "open": "58.36",
      |    "low": "57.24",
      |    "high": "58.69",
      |    "volume": "1.27",
      |    "volumeQuote": "72.9492",
      |    "timestamp": "2018-10-06T14:50:51.632Z",
      |    "symbol": "LTCDAI"
      |  },
      |  {
      |    "ask": "99.38",
      |    "bid": "98.92",
      |    "last": "99.83",
      |    "open": "99.31",
      |    "low": "99.83",
      |    "high": "99.89",
      |    "volume": "3.287",
      |    "volumeQuote": "328.26862",
      |    "timestamp": "2018-10-06T14:50:42.915Z",
      |    "symbol": "XMREURS"
      |  }
      |]
    """.stripMargin

  "availableBooks" should {
    "retrieve available books" in {
      val expectedBooks =
        "BTC_LTC ETH_LTC USD_LTC USDT_XRP EOS_XMR DAI_LTC EURS_XMR".split(" ").map(Book.fromString).map(_.get).toList

      mockRequest(responseBody)
      whenReady(service.availableBooks()) { books =>
        books.size mustEqual expectedBooks.size

        books.sortBy(_.string) mustEqual expectedBooks.sortBy(_.string)
      }
    }

    "ignore entries having null on the last price" in {
      val responseBody =
        """
          |[
          |  {
          |    "ask": "99.38",
          |    "bid": "98.92",
          |    "last": "99.83",
          |    "open": "99.31",
          |    "low": "99.83",
          |    "high": "99.89",
          |    "volume": "3.287",
          |    "volumeQuote": "328.26862",
          |    "timestamp": "2018-10-06T14:50:42.915Z",
          |    "symbol": "XMREURS"
          |  },
          |  {
          |    "ask": null,
          |    "bid": "98.92",
          |    "last": null,
          |    "open": null,
          |    "low": "99.83",
          |    "high": "99.89",
          |    "volume": "3.287",
          |    "volumeQuote": "328.26862",
          |    "timestamp": "2018-10-06T14:50:42.915Z",
          |    "symbol": "LTCEURS"
          |  }
          |]
        """.stripMargin

      val expectedBooks = "EURS_XMR"
        .split(" ")
        .map(Book.fromString)
        .map(_.get)
        .toList

      mockRequest(responseBody)
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
          "USDT_XRP" -> BigDecimal("1.2130"),
          "EOS_XMR" -> BigDecimal("19.777"),
          "DAI_LTC" -> BigDecimal("57.25"),
          "EURS_XMR" -> BigDecimal("99.83")
      ).map { case (string, price) => Ticker(Book.fromString(string).get, price) }

      mockRequest(responseBody)
      whenReady(service.getTickerList()) { tickerList =>
        tickerList.size mustEqual expectedTickers.size

        tickerList.sortBy(_.book.string) mustEqual expectedTickers.sortBy(_.book.string)
      }
    }
  }

  private def mockRequest(result: String) = {
    val request = mock[WSRequest]
    val response = mock[WSResponse]
    val json = Json.parse(result)

    when(ws.url(anyString)).thenReturn(request)
    when(response.status).thenReturn(200)
    when(response.json).thenReturn(json)
    when(request.get()).thenReturn(Future.successful(response))
  }
}
