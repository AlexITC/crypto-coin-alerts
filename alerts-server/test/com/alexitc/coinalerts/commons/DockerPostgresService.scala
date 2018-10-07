package com.alexitc.coinalerts.commons

import java.sql.DriverManager

import com.whisk.docker.{DockerCommandExecutor, DockerContainer, DockerContainerState, DockerKit, DockerReadyChecker}

import scala.concurrent.ExecutionContext

object DockerPostgresService {

  val PostgresImage = "postgres:9.6"
  val PostgresUsername = "postgres"
  val PostgresPassword = ""
  val DatabaseName = "crypto_coin_alerts"

  def PostgresAdvertisedPort = 5432
  def PostgresExposedPort = 44444

  class PostgresReadyChecker extends DockerReadyChecker {

    override def apply(
        container: DockerContainerState)(implicit docker: DockerCommandExecutor, ec: ExecutionContext) = {

      container
        .getPorts()
        .map { ports =>
          try {
            Class.forName("org.postgresql.Driver")
            val url = s"jdbc:postgresql://${docker.host}:$PostgresExposedPort/"
            Option(DriverManager.getConnection(url, PostgresUsername, PostgresPassword))
              .foreach { conn =>
                // NOTE: For some reason the result is always false
                conn.createStatement().execute(s"CREATE DATABASE $DatabaseName")
                conn.close()
              }

            true
          } catch {
            case _: Throwable =>
              false
          }
        }
    }
  }
}

trait DockerPostgresService extends DockerKit {

  import DockerPostgresService._

  import scala.concurrent.duration._

  val postgresContainer = DockerContainer(PostgresImage)
    .withPorts((PostgresAdvertisedPort, Some(PostgresExposedPort)))
    .withEnv(s"POSTGRES_USER=$PostgresUsername", s"POSTGRES_PASSWORD=$PostgresPassword")
    .withReadyChecker(
        new PostgresReadyChecker().looped(15, 1.second)
    )

  abstract override def dockerContainers: List[DockerContainer] =
    postgresContainer :: super.dockerContainers
}
