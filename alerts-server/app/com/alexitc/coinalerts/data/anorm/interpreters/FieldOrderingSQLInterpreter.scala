package com.alexitc.coinalerts.data.anorm.interpreters

import com.alexitc.playsonify.models.{FieldOrdering, OrderingCondition}

class FieldOrderingSQLInterpreter {

  def toOrderByClause[A](fieldOrdering: FieldOrdering[A])(implicit columnNameResolver: ColumnNameResolver[A]) = {
    val field = columnNameResolver.getColumnName(fieldOrdering.field)
    val condition = getCondition(fieldOrdering.orderingCondition)

    s"ORDER BY $field $condition"
  }

  private def getCondition(ordering: OrderingCondition) = ordering match {
    case OrderingCondition.AscendingOrder => "ASC"
    case OrderingCondition.DescendingOrder => "DESC"
  }
}
