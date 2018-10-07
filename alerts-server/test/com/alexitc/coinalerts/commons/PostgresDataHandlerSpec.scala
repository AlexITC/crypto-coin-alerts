package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.data.anorm.dao.{ExchangeCurrencyPostgresDAO, UserPostgresDAO}
import com.alexitc.coinalerts.data.anorm.{ExchangeCurrencyPostgresDataHandler, UserPostgresDataHandler}
import com.spotify.docker.client.DefaultDockerClient
import com.whisk.docker.DockerFactory
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}
import play.api.db.evolutions.Evolutions
import play.api.db.{Database, Databases}

/**
 * Allow us to write integration tests depending in a postgres database.
 *
 * The database is launched in a docker instance using docker-it-scala library.
 *
 * When the database is started, play evolutions are automatically applied, the
 * idea is to let you write tests like this:
 * {{{
 *   class UserPostgresDALSpec extends PostgresDALSpec {
 *     lazy val dal = new UserPostgresDAL(database)
 *     ...
 *   }
 * }}}
 */
trait PostgresDataHandlerSpec
    extends WordSpec
    with MustMatchers
    with DockerTestKit
    with DockerPostgresService
    with BeforeAndAfterAll {

  import DockerPostgresService._

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  override implicit val dockerFactory: DockerFactory = new SpotifyDockerFactory(DefaultDockerClient.fromEnv().build())

  override def beforeAll(): Unit = {
    super.beforeAll()
    val _ = isContainerReady(postgresContainer).futureValue mustEqual true
  }

  def seedCurrencies: Boolean = true

  def database: Database = {
    val database = Databases(
        driver = "org.postgresql.Driver",
        url = s"jdbc:postgresql://localhost:$PostgresExposedPort/$DatabaseName",
        name = "default",
        config = Map(
            "username" -> PostgresUsername,
            "password" -> PostgresPassword
        )
    )

    Evolutions.applyEvolutions(database)

    // currencies is a core feature required by most tests
    // the data handler is created here to avoid a cyclic dependency
    if (seedCurrencies) {
      CurrencySeeder.seed(new ExchangeCurrencyPostgresDataHandler(database, new ExchangeCurrencyPostgresDAO))
    }

    database
  }

  implicit lazy val userDataHandler = new UserPostgresDataHandler(database, new UserPostgresDAO)
  implicit lazy val exchangeCurrencyDataHandler =
    new ExchangeCurrencyPostgresDataHandler(database, new ExchangeCurrencyPostgresDAO)
}
