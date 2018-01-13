package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.models.{FixedPriceAlertFilter, UserId}

class FixedPriceAlertFilterParser {

  /**
   * accepts comma separated filters like:
   * - triggered=true|false|*
   *
   * empty string is also accepted.
   */
  def from(string: String, userId: UserId): Option[FixedPriceAlertFilter.Conditions] = {
    val filters = string.split(",").map { dirtyFilter =>
      Filter.from(dirtyFilter)
          .flatMap(toCondition)
    }

    if (string.isEmpty || filters.forall(_.isDefined)) {
      // TODO: validate that each key is present at most once
      val conditions = from(filters.flatten, userId)
      Some(conditions)
    } else {
      None
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
    Option(string.split("="))
        .filter(_.length == 2)
        .map { case Array(key, value) =>
          Filter(key, value)
        }
  }
}
