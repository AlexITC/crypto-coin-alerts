package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.core.{AuthorizationToken, PaginatedQuery}
import com.alexitc.coinalerts.data._
import com.alexitc.coinalerts.modules.{ExchangeCurrencySeederTaskModule, FixedPriceAlertsTaskModule}
import com.alexitc.coinalerts.services.external.ReCaptchaService
import com.alexitc.coinalerts.services.{EmailServiceTrait, JWTService}
import com.alexitc.playsonify.test.PlayAPISpec
import org.slf4j.LoggerFactory
import play.api.db.{DBApi, Database, Databases}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, Mode}

import scala.concurrent.Future

/**
 * A PlayAPISpec allow us to write tests for the API calls
 * without depending on a database but custom implementations
 * for the data layer.
 */
trait CustomPlayAPISpec extends PlayAPISpec {

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

  /***********************************************************************/

  implicit val userDataHandler: UserBlockingDataHandler = new UserInMemoryDataHandler {}

  // exchange currencies is a core feature required by most tests
  implicit val exchangeCurrencyDataHandler: ExchangeCurrencyBlockingDataHandler = new ExchangeCurrencyInMemoryDataHandler {}
  CurrencySeeder.seed

  override val guiceApplicationBuilder: GuiceApplicationBuilder = GuiceApplicationBuilder(loadConfiguration = loadConfigWithoutEvolutions)
      .in(Mode.Test)
      .disable(classOf[FixedPriceAlertsTaskModule])
      .disable(classOf[ExchangeCurrencySeederTaskModule])
      .overrides(bind[Database].to(dummyDB))
      .overrides(bind[DBApi].to(dummyDBApi))
      .overrides(bind[EmailServiceTrait].to(new FakeEmailService))
      .overrides(bind[ReCaptchaService].to(new FakeReCaptchaService))
      .overrides(bind[UserBlockingDataHandler].to(userDataHandler))
      .overrides(bind[ExchangeCurrencyBlockingDataHandler].to(exchangeCurrencyDataHandler))

  lazy val jwtService = application.injector.instanceOf[JWTService]

  override protected def log[T](request: FakeRequest[T], response: Future[Result]) = {
    logger.info(s"REQUEST > $request, headers = ${request.headers}; RESPONSE < status = ${status(response)}, body = ${contentAsString(response)}")
  }
}

object CustomPlayAPISpec {

  import PlayAPISpec.Implicits._

  implicit class AuthorizationTokenExt(val token: AuthorizationToken) extends AnyVal {
    def toHeader: (String, String) = AUTHORIZATION -> s"Bearer ${token.string}"
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
