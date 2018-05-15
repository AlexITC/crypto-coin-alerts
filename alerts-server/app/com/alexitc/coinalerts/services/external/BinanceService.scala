package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.config.ExternalServiceExecutionContext
import com.alexitc.coinalerts.models.{Book, Currency, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

class BinanceService @Inject() (
    ws: WSClient)(
    implicit ec: ExternalServiceExecutionContext)
    extends ExchangeService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BaseURL = "https://api.binance.com/api"

  /**
   * Binance gives us a book without a separator between the market and the currency,
   * for example: DASHBTC means BTC as market and DASH as currency.
   *
   * Then, we need to know the supported markets in advance to perform this parsing.
   */
  private val KnownMarkets = "BTC ETH USDT BNB".split(" ").flatMap(Market.from).toList

  override def availableBooks(): Future[List[Book]] = {
    getTickerList()
        .map { tickerList =>
          tickerList.map(_.book)
        }
  }

  override def getTickerList(): Future[List[Ticker]] = {
    val url = s"$BaseURL/v1/ticker/allPrices"

    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .flatMap(toJson)
              .flatMap { json =>
                json
                    .asOpt[List[Option[Ticker]]]
                    .map(_.flatten)
              }
              .getOrElse {
                logger.warn(s"Unexpected response from BINANCE, status = [${response.status}]")
                List.empty
              }
        }

  }

  private def toJson(response: WSResponse) = {
    if (response.status != 200) {
      None
    } else {
      val json = response.json
      Some(json)
    }
  }

  private implicit val tickerReads: Reads[Option[Ticker]] = {
    val builder = (JsPath \ "symbol").read[String].map(_.toUpperCase).map(createBook) and
        (JsPath \ "price").read[BigDecimal]

    builder.apply { (bookMaybe, currentPrice) =>
      bookMaybe.map { book =>
        Ticker(book, currentPrice)
      }
    }
  }

  private def createBook(string: String): Option[Book] = {
    KnownMarkets
        .find { market => string.endsWith(market.string) }
        .map { market =>
          val currencyStr = string.substring(0, string.length - market.string.length)
          val currency = Currency(currencyStr)
          Book(market, currency)
        }
        .orElse {
          logger.warn(s"Unable to create book from string = [$string]")
          None
        }
  }
}
