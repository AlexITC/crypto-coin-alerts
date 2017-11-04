package com.alexitc.coinalerts.common

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.db.{DBApi, Database, Databases}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Environment, Mode}

/**
 * A PlayAPISpec allow us to write tests for the API calls
 * without depending on a database but custom implementations
 * for the data layer.
 */
trait PlayAPISpec extends PlaySpec with ScalaFutures with MockitoSugar {

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

  val guiceApplicationBuilder: GuiceApplicationBuilder = GuiceApplicationBuilder(loadConfiguration = loadConfigWithoutEvolutions)
      .in(Mode.Test)
      .overrides(bind[Database].to(dummyDB))
      .overrides(bind[DBApi].to(dummyDBApi))
}
