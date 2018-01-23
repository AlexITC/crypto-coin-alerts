package com.alexitc.coinalerts.services.external

import javax.inject.Inject

import com.alexitc.coinalerts.models.{BitsoBook, Book, Currency, Market}
import com.alexitc.coinalerts.tasks.models.Ticker
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

class KucoinService @Inject() (ws: WSClient)(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BaseURL = "https://api.kucoin.com"

  def availableBooks(): Future[List[Book]] = {
    val url = s"$BaseURL/v1/open/tick"

    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .flatMap(toJson)
              .map { json =>
                val resultList = (json \ "data")
                    .as[List[JsValue]]

                resultList.flatMap { result =>
                  val marketMaybe = (result \ "coinTypePair").asOpt[String].map(Market.apply)
                  val currencyMaybe = (result \ "coinType").asOpt[String].map(Currency.apply)
                  for (market <- marketMaybe; currency <- currencyMaybe)
                    yield Book(market, currency)
                }
              }.getOrElse {
            logger.warn(s"Unexpected response from KUCOIN, status = [${response.status}]")
            List.empty
          }
        }
  }

  def getTickerList(): Future[List[Ticker]] = {
    val url = s"$BaseURL/v1/open/tick"

    ws.url(url)
        .get()
        .map { response =>
          Option(response)
              .flatMap(toJson)
              .flatMap { json =>
                (json \ "data")
                    .asOpt[List[Option[Ticker]]]
                    .map(_.flatten)
              }
              .getOrElse {
                logger.warn(s"Unexpected response from KUCOIN, status = [${response.status}]")
                List.empty
              }
        }

  }

  private def toJson(response: WSResponse) = {
    if (response.status != 200) {
      None
    } else {
      val json = response.json
      (json \ "success")
          .asOpt[Boolean]
          .filter(identity)
          .map(_ => json)
    }
  }

  private implicit val tickerReads: Reads[Option[Ticker]] = {
    val builder = (JsPath \ "symbol").read[String].map(createBook) and
        (JsPath \ "lastDealPrice").read[BigDecimal]

    builder.apply { (bookMaybe, currentPrice) =>
      bookMaybe.map { book =>
        Ticker(book, currentPrice)
      }
    }
  }

  private def createBook(string: String): Option[Book] = {
    // the book format is reversed to the one in our app
    BitsoBook.fromString(string.replace("-", "_"))
        .orElse {
          logger.warn(s"Unable to create book from string = [$string]")
          None
        }
  }
}
