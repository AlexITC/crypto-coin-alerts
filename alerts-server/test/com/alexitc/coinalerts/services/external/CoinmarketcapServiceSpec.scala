package com.alexitc.coinalerts.services.external

import com.alexitc.coinalerts.models.{Book, Currency, CurrencyName, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future

class CoinmarketcapServiceSpec  extends WordSpec with MustMatchers with ScalaFutures with MockitoSugar {

  val ws = mock[WSClient]
  val ec = scala.concurrent.ExecutionContext.global
  val service = new CoinmarketcapService(ws)(ec)

  val responseBody =
    """
      |[
      |    {
      |        "id": "bitcoin",
      |        "name": "Bitcoin",
      |        "symbol": "BTC",
      |        "rank": "1",
      |        "price_usd": "11053.8",
      |        "price_btc": "1.0",
      |        "24h_volume_usd": "10867700000.0",
      |        "market_cap_usd": "185930022856",
      |        "available_supply": "16820462.0",
      |        "total_supply": "16820462.0",
      |        "max_supply": "21000000.0",
      |        "percent_change_1h": "1.37",
      |        "percent_change_24h": "-5.17",
      |        "percent_change_7d": "-16.7",
      |        "last_updated": "1516679961"
      |    },
      |    {
      |        "id": "ethereum",
      |        "name": "Ethereum",
      |        "symbol": "ETH",
      |        "rank": "2",
      |        "price_usd": "1013.98",
      |        "price_btc": "0.0920443",
      |        "24h_volume_usd": "3890510000.0",
      |        "market_cap_usd": "98510477207.0",
      |        "available_supply": "97152288.0",
      |        "total_supply": "97152288.0",
      |        "max_supply": null,
      |        "percent_change_1h": "1.09",
      |        "percent_change_24h": "-4.15",
      |        "percent_change_7d": "-17.75",
      |        "last_updated": "1516679952"
      |    }
      |]
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

      whenReady(service.availableBooks()) { books =>
        books.size mustEqual 3

        val expectedBooks = List(
          Book(Market("USD"), Currency("BTC"), Some(CurrencyName("Bitcoin"))),
          Book(Market("USD"), Currency("ETH"), Some(CurrencyName("Ethereum"))),
          Book(Market("BTC"), Currency("ETH"), Some(CurrencyName("Ethereum")))
        )

        expectedBooks.foreach { expectedBook =>
          books.contains(expectedBook) mustEqual true
        }
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
        tickerList.size mustEqual 3

        val expectedTickerList = List(
          Ticker(Book(Market("USD"), Currency("BTC"), Some(CurrencyName("Bitcoin"))), BigDecimal("11053.8")),
          Ticker(Book(Market("USD"), Currency("ETH"), Some(CurrencyName("Ethereum"))), BigDecimal("1013.98")),
          Ticker(Book(Market("BTC"), Currency("ETH"), Some(CurrencyName("Ethereum"))), BigDecimal("0.0920443"))
        )

        expectedTickerList.foreach { expectedTicker =>
          tickerList.contains(expectedTicker) mustEqual true
        }
      }
    }
  }
}
