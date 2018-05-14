package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.ServerError
import org.postgresql.util.PSQLException

sealed trait PostgresError extends ServerError

case class PostgresIntegrityViolationError(column: Option[String], psqlException: PSQLException) extends PostgresError {

  override def cause: Option[Throwable] = Option(psqlException)
}
