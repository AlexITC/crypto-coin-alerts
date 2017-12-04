package com.alexitc.coinalerts.commons

import java.net.URLEncoder

import com.alexitc.coinalerts.core.{AuthorizationToken, PaginatedQuery}
import com.alexitc.coinalerts.models.Book
import com.alexitc.coinalerts.modules.AlertTaskModule
import com.alexitc.coinalerts.services.EmailServiceTrait
import com.alexitc.coinalerts.services.validators.{BitsoBookValidator, BittrexBookValidator}
import org.scalactic.Good
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.slf4j.LoggerFactory
import play.api.db.{DBApi, Database, Databases}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Configuration, Environment, Mode}

import scala.concurrent.Future

/**
 * A PlayAPISpec allow us to write tests for the API calls
 * without depending on a database but custom implementations
 * for the data layer.
 */
trait PlayAPISpec extends PlaySpec with ScalaFutures {

  private val logger = LoggerFactory.getLogger(this.getClass)

  /**
   * A dummy [[Database]] and [[DBApi]] just to allow a play application
   * to start without connecting to a real database from application.conf.
   */
  private val dummyDB = Databases.inMemory()
  private val dummyDBApi = new DBApi {
    override def databases(): Seq[Database] = List(dummyDB)
    override def database(name: String): Database = dummyDB
    override def shutdown(): Unit = dummyDB.shutdown()
  }

  /**
   * Loads configuration disabling evolutions on default database.
   *
   * This allows to not write a custom application.conf for testing
   * and ensure play evolutions are disabled.
   */
  private def loadConfigWithoutEvolutions(env: Environment): Configuration = {
    val map = Map("play.evolutions.db.default.enabled" -> false)

    Configuration.load(env) ++ Configuration.from(map)
  }

  private val fakeBitsoBookValidator = new BitsoBookValidator {
    override protected def availableBooks: List[Book] = ???

    override def validateBook(book: Book): ApplicationResult[Book] = Good(book)
  }

  private val fakeBittrexBookValidator = new BittrexBookValidator {
    override protected def availableBooks: List[Book] = ???

    override def validateBook(book: Book): ApplicationResult[Book] = Good(book)
  }

  val guiceApplicationBuilder: GuiceApplicationBuilder = GuiceApplicationBuilder(loadConfiguration = loadConfigWithoutEvolutions)
      .in(Mode.Test)
      .disable(classOf[AlertTaskModule])
      .overrides(bind[Database].to(dummyDB))
      .overrides(bind[DBApi].to(dummyDBApi))
      .overrides(bind[EmailServiceTrait].to(new FakeEmailService))
      .overrides(bind[BitsoBookValidator].to(fakeBitsoBookValidator))
      .overrides(bind[BittrexBookValidator].to(fakeBittrexBookValidator))

  def application: Application

  private val JsonHeader = CONTENT_TYPE -> "application/json"
  private val EmptyJson = "{}"

  private def logRequestResponse[T](request: FakeRequest[T], response: Future[Result]) = {
    logger.info(s"REQUEST > $request, headers = ${request.headers}; RESPONSE < status = ${status(response)}, body = ${contentAsString(response)}")
  }

  /** Syntactic sugar for calling APIs **/
  def POST(url: String, jsonBody: Option[String], extraHeaders: (String, String)*): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val json = jsonBody.getOrElse(EmptyJson)
    val request = FakeRequest("POST", url)
        .withHeaders(headers: _*)
        .withBody(json)

    val response = route(application, request).get
    logRequestResponse(request, response)
    response
  }

  def POST(url: String, extraHeaders: (String, String)*): Future[Result] = {
    POST(url, None, extraHeaders: _*)
  }

  def GET(url: String, extraHeaders: (String, String)*): Future[Result] = {
    val headers = JsonHeader :: extraHeaders.toList
    val request = FakeRequest("GET", url)
        .withHeaders(headers: _*)

    val response = route(application, request).get
    logRequestResponse(request, response)
    response
  }
}

object PlayAPISpec {
  implicit class AuthorizationTokenExt(val token: AuthorizationToken) extends AnyVal {
    def toHeader: (String, String) = AUTHORIZATION -> s"Bearer ${token.string}"
  }

  implicit class HttpExt(val params: List[(String, String)]) extends AnyVal {
    def toQueryString: String = {
      params
          .map { case (key, value) =>
            val encodedKey = URLEncoder.encode(key, "UTF-8")
            val encodedValue = URLEncoder.encode(value, "UTF-8")
            List(encodedKey, encodedValue).mkString("=")
          }
          .mkString("&")
    }
  }

  implicit class PaginatedQueryExt(val query: PaginatedQuery) extends AnyVal {
    def toHttpQueryString: String = {
      val params = List(
        "offset" -> query.offset.int.toString,
        "limit" -> query.limit.int.toString
      )

      params.toQueryString
    }
  }

  implicit class StringUrlExt(val url: String) extends AnyVal {
    def withQueryParams(params: (String, String)*): String = {
      List(url, params.toList.toQueryString).mkString("?")
    }

    def withQueryParams(query: PaginatedQuery): String = {
      List(url, query.toHttpQueryString).mkString("?")
    }
  }
}
