package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.core.OrderByQuery
import com.alexitc.coinalerts.errors.InvalidOrderError
import com.alexitc.coinalerts.models.FixedPriceAlertOrderBy
import com.alexitc.coinalerts.models.FixedPriceAlertOrderBy.{Conditions, DescendingOrderCondition, OrderByCreatedOn}
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Bad, Good, One, Or}

class FixedPriceAlertOrderByParser {

  import FixedPriceAlertOrderBy._
  import FixedPriceAlertOrderByParser._

  /**
   * Accepts values in the format field[:condition], being condition
   * an optional argument, for example:
   * - createdOn
   * - currency:desc
   * - exchange:asc
   *
   * The empty string is also accepted returning a default ordering.
   */
  def from(orderByQuery: OrderByQuery): ApplicationResult[Conditions] = {
    Option(orderByQuery.string)
        .filter(_.nonEmpty)
        .map { string => from(string.split(":")) }
        .getOrElse { Good(DefaultConditions) }
  }

  private def from(parts: Seq[String]) = parts match {
    case Seq(unsafeOrderBy) =>
      val conditionsMaybe = parseOrderBy(unsafeOrderBy)
          .map { orderBy => DefaultConditions.copy(orderBy = orderBy) }

      Or.from(conditionsMaybe, One(InvalidOrderError))

    case Seq(unsafeOrderBy, unsafeOrderCondition) =>
      val conditionsMaybe = for {
        orderBy <- parseOrderBy(unsafeOrderBy)
        orderCondition <- parseOrderCondition(unsafeOrderCondition)
      } yield Conditions(orderBy, orderCondition)

      Or.from(conditionsMaybe, One(InvalidOrderError))

    case _ =>
      Bad(InvalidOrderError).accumulating
  }

  private def parseOrderBy(string: String) = string match {
    case "createdOn" => Some(OrderByCreatedOn)
    case "currency" => Some(OrderByCurrency)
    case "exchange" => Some(OrderByExchange)
    case _ => None
  }

  private def parseOrderCondition(string: String) = string match {
    case "asc" => Some(AscendingOrderCondition)
    case "desc" => Some(DescendingOrderCondition)
    case _ => None
  }
}

object FixedPriceAlertOrderByParser {

  val DefaultConditions = Conditions(OrderByCreatedOn, DescendingOrderCondition)

}