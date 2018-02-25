package com.alexitc.coinalerts.errors

import com.alexitc.playsonify.models.ServerError
import org.postgresql.util.PSQLException

sealed trait PostgresError extends ServerError {
  def cause: PSQLException
}
case class PostgresIntegrityViolationError(column: Option[String], cause: PSQLException) extends PostgresError
