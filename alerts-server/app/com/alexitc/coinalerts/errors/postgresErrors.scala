package com.alexitc.coinalerts.errors

import com.alexitc.coinalerts.commons.ServerError
import org.postgresql.util.PSQLException

// PostgreSQL specific errors
sealed trait PostgresError extends ServerError {
  def cause: PSQLException
}
case class PostgresIntegrityViolationError(column: Option[String], cause: PSQLException) extends PostgresError
