package com.alexitc.coinalerts.data.anorm.interpreters

import com.alexitc.coinalerts.models.FixedPriceAlertOrderBy

class FixedPriceAlertOrderBySQLInterpreter {

  import FixedPriceAlertOrderBy._

  def toSQL(conditions: Conditions) = {
    val field = getField(conditions.orderBy)
    val condition = getCondition(conditions.orderCondition)

    s"ORDER BY $field $condition"
  }

  private def getField(orderBy: FixedPriceAlertOrderBy.OrderBy) = orderBy match  {
    case OrderByCreatedOn => "created_on"
    case OrderByExchange => "exchange"
    case OrderByCurrency => "currency"
  }

  private def getCondition(condition: FixedPriceAlertOrderBy.OrderCondition) = condition match  {
    case AscendingOrderCondition => "ASC"
    case DescendingOrderCondition => "DESC"
  }
}
