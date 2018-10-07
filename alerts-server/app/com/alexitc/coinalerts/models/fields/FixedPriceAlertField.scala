package com.alexitc.coinalerts.models.fields

import com.alexitc.coinalerts.data.anorm.interpreters.ColumnNameResolver
import enumeratum._

sealed abstract class FixedPriceAlertField(override val entryName: String) extends EnumEntry

object FixedPriceAlertField extends Enum[FixedPriceAlertField] {

  val values = findValues

  case object CreatedOn extends FixedPriceAlertField("createdOn")
  case object Currency extends FixedPriceAlertField("currency")
  case object Exchange extends FixedPriceAlertField("exchange")

  implicit val columnNameResolver: ColumnNameResolver[FixedPriceAlertField] = (field) =>
    field match {
      case CreatedOn => "created_on"
      case x => x.entryName
  }
}
