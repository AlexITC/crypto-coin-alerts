package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.models.{Book, Currency, CurrencyName, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.slf4j.LoggerFactory
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

class CoinmarketcapService @Inject() (ws: WSClient)(implicit ec: ExecutionContext) extends ExchangeService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BTCMarket = Market("BTC")
  private val USDMarket = Market("USD")

  private val BaseURL = "https://api.coinmarketcap.com"

  override def availableBooks(): Future[List[Book]] = {
    getTickerList().map { ticketList =>
      ticketList.map(_.book)
    }
  }

  override def getTickerList(): Future[List[Ticker]] = {
    val url = s"$BaseURL/v1/ticker/?limit=2000"

    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .flatMap(toJson)
              .map { jsonList =>
                jsonList.flatMap { json =>
                  toTickerList(json)
                }
              }
              .getOrElse {
                logger.warn(s"Unexpected response from COINMARKETCAP, status = [${response.status}]")
                List.empty
              }
        }

  }

  private def toJson(response: WSResponse) = {
    if (response.status != 200) {
      None
    } else {
      response.json.asOpt[List[JsValue]]
    }
  }

  // coinmarketcap give us prices in BTC and USD
  private def toTickerList(json: JsValue): List[Ticker] = {
    val result = for {
      // while symbol field would be preferred, there are collisions
      currencyString <- (json \ "symbol").asOpt[String]
      priceUSD <- (json \ "price_usd").asOpt[BigDecimal]
      priceBTC <- (json \ "price_btc").asOpt[BigDecimal]
      currencyName <- (json \ "name").asOpt[String]
          .map(_.trim)
          .filter(_.nonEmpty)
          .map(CurrencyName.apply)
    } yield {
      val currency = Currency(currencyString)
      val tickerUSD = Ticker(Book(USDMarket, currency, Some(currencyName)), priceUSD)

      if ("BTC" equalsIgnoreCase currencyString) {
        // there is no need to match BTC price against BTC
        List(tickerUSD)
      } else {
        val tickerBTC = Ticker(Book(BTCMarket, currency, Some(currencyName)), priceBTC)
        List(tickerBTC, tickerUSD)
      }
    }

    result.getOrElse {
      logger.warn(s"There was an error while mapping a value to a ticker, json = [$json]")
      List.empty
    }
  }
}
