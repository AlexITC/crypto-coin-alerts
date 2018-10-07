package com.alexitc.coinalerts.data.anorm.interpreters

import scala.annotation.implicitNotFound

@implicitNotFound(
    "No column name resolver found for type ${A}. Try to implement an implicit ColumnNameResolver for this type."
)
trait ColumnNameResolver[A] {

  def getColumnName(field: A): String
}
