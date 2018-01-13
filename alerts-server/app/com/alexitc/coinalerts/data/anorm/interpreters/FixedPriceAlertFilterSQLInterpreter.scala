package com.alexitc.coinalerts.data.anorm.interpreters

import com.alexitc.coinalerts.models.FixedPriceAlertFilter

class FixedPriceAlertFilterSQLInterpreter {

  import FixedPriceAlertFilter._

  /**
   * Transform the [[Conditions]] into a SQL WHERE clause
   * that is executable by anorm.
   */
  def toWhere(conditions: Conditions): WhereCondition = {
    val clauses = List(conditions.triggered, conditions.user).flatMap {
      case condition: UserCondition =>
        toSQLClause(condition)

      case condition: TriggeredCondition =>
        toSQLClause(condition)
    }

    if (clauses.isEmpty) {
      WhereCondition.Empty
    } else {
      val clausesSQL = clauses.map(_.sql).mkString(" AND ")
      val params = clauses.flatMap(_.params)
      new WhereCondition(s"WHERE $clausesSQL", params)
    }
  }

  private def toSQLClause(condition: FixedPriceAlertFilter.UserCondition): Option[SQLClause] = condition match {
    case AnyUserCondition => None
    case JustThisUserCondition(userId) => Some(SQLClause("user_id = {user_id}", "user_id" -> userId.string))
  }

  private def toSQLClause(condition: TriggeredCondition): Option[SQLClause] = condition match {
    case AnyTriggeredCondition => None
    case HasBeenTriggeredCondition => Some(SQLClause("triggered_on IS NOT NULL"))
    case HasNotBeenTriggeredCondition => Some(SQLClause("triggered_on IS NULL"))
  }
}

/**
 * [[sql]] should be a valid SQL executable by anorm, valid SQL could be:
 * - empty string (no conditions required)
 * - string starting with "WHERE" like "WHERE triggered_on IS NULL"
 *
 * The [[sql]] could require unsafe arguments which are provided in the
 * [[params]] list.
 */
class WhereCondition private[interpreters] (val sql: String, val params: List[(String, String)])
object WhereCondition {
  val Empty = new WhereCondition("", List.empty)
}

/**
 * A simple clause that could be used in the WHERE condition, for example:
 * - used_id = {user_id}
 *
 * When the clause requires an unsafe argument, it is provided in the [[params]]
 * list.
 */
case class SQLClause(sql: String, params: (String, String)*)
