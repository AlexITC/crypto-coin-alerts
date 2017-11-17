package com.alexitc.coinalerts.data.anorm

import java.sql.Connection

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{PostgresError, PostgresIntegrityViolationError}
import org.postgresql.util.PSQLException
import org.scalactic.Bad
import play.api.db.Database

/**
 * Allow us to map a [[PSQLException]] to a sub type of [[PostgresError]].
 *
 * This is helpful to differentiate between errors caused by input data
 * and failures that can not be prevented, these failures are thrown.
 *
 * The errors are mapped based on postgres error codes:
 * - see: https://www.postgresql.org/docs/9.6/static/errcodes-appendix.html
 */
trait AnormPostgresDAL {

  protected def database: Database

  def withConnection[A](block: Connection => ApplicationResult[A]): ApplicationResult[A] = {
    try {
      database.withConnection(block)
    } catch {
      case e: PSQLException if isIntegrityConstraintViolationError(e) =>
        Bad(PostgresIntegrityViolationError(e)).accumulating
    }
  }

  def withTransaction[A](block: Connection => ApplicationResult[A]): ApplicationResult[A] = {
    try {
      database.withTransaction(block)
    } catch {
      case e: PSQLException if isIntegrityConstraintViolationError(e) =>
        Bad(PostgresIntegrityViolationError(e)).accumulating
    }
  }

  private def isIntegrityConstraintViolationError(e: PSQLException) = e.getSQLState startsWith "23"

}
