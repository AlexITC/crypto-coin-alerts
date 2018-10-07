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

class BinanceServiceSpec extends WordSpec with MustMatchers with ScalaFutures with MockitoSugar {

  val ws = mock[WSClient]
  val ec = ExecutionContexts.externalServiceEC
  val service = new BinanceService(ws)(ec)

  val responseBody =
    """
      |[
      |  {
      |    "symbol": "DASHBTC",
      |    "price": "0.06732200"
      |  },
      |  {
      |    "symbol": "DASHETH",
      |    "price": "0.69579000"
      |  },
      |  {
      |    "symbol": "LTCUSDT",
      |    "price": "181.63000000"
      |  },
      |  {
      |    "symbol": "LTCBNB",
      |    "price": "13.62000000"
      |  }
      |]
    """.stripMargin

  "availableBooks" should {
    "retrieve available books" in {
      val expectedBooks = "BTC_DASH ETH_DASH USDT_LTC BNB_LTC".split(" ").map(Book.fromString).map(_.get).toList

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
          "BTC_DASH" -> BigDecimal("0.06732200"),
          "ETH_DASH" -> BigDecimal("0.69579000"),
          "USDT_LTC" -> BigDecimal("181.63000000"),
          "BNB_LTC" -> BigDecimal("13.62000000")
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
