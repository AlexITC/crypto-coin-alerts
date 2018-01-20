package com.alexitc.coinalerts.models

object FixedPriceAlertOrderBy {

  sealed trait OrderBy
  case object OrderByExchange extends OrderBy
  case object OrderByCurrency extends OrderBy
  case object OrderByCreatedOn extends OrderBy

  sealed trait OrderCondition
  case object DescendingOrderCondition extends OrderCondition
  case object AscendingOrderCondition extends OrderCondition

  case class Conditions(orderBy: OrderBy, orderCondition: OrderCondition)
}
