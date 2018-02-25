package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.core.FilterQuery
import com.alexitc.coinalerts.errors.InvalidFilterError
import com.alexitc.coinalerts.models.{FixedPriceAlertFilter, UserId}
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Bad, Good}

class FixedPriceAlertFilterParser {

  /**
   * accepts comma separated filters like:
   * - triggered=true|false|*
   *
   * empty string is also accepted.
   */
  def from(filterQuery: FilterQuery, userId: UserId): ApplicationResult[FixedPriceAlertFilter.Conditions] = {
    val filters = Option(filterQuery.string)
        .filter(_.nonEmpty)
        .map(_.split(","))
        .map { dirtyFilters =>
          dirtyFilters.map {
            Filter.from(_)
                .flatMap(toCondition)
          }
        }
        .getOrElse(Array.empty)


    if (filters.forall(_.isDefined)) {
      // TODO: validate that each key is present at most once
      val conditions = from(filters.flatten, userId)
      Good(conditions)
    } else {
      Bad(InvalidFilterError).accumulating
    }
  }

  private def from(filters: Seq[FixedPriceAlertFilter.Condition], userId: UserId) = {
    val triggered = filters.collect {
      case t: FixedPriceAlertFilter.TriggeredCondition => t
    }.headOption.getOrElse(FixedPriceAlertFilter.AnyTriggeredCondition)

    val user = FixedPriceAlertFilter.JustThisUserCondition(userId)

    FixedPriceAlertFilter.Conditions(triggered, user)
  }

  private def toCondition(filter: Filter): Option[FixedPriceAlertFilter.Condition] = filter.key match {
    case "triggered" => filter.value match {
      case "*" => Some(FixedPriceAlertFilter.AnyTriggeredCondition)
      case "true" => Some(FixedPriceAlertFilter.HasBeenTriggeredCondition)
      case "false" => Some(FixedPriceAlertFilter.HasNotBeenTriggeredCondition)
      case _ => None
    }

    case _ => None
  }
}


case class Filter(key: String, value: String) {
  override def toString: String = s"$key=$value"
}
object Filter {
  def from(string: String): Option[Filter] = {
    Option(string.split(":"))
        .filter(_.length == 2)
        .map { case Array(key, value) =>
          Filter(key, value)
        }
  }
}
