package com.alexitc.coinalerts.parsers

import com.alexitc.coinalerts.models.fields.FixedPriceAlertField
import com.alexitc.playsonify.models.OrderingCondition
import com.alexitc.playsonify.parsers.FieldOrderingParser

class FixedPriceAlertOrderingParser extends FieldOrderingParser[FixedPriceAlertField] {

  override protected def defaultField: FixedPriceAlertField = FixedPriceAlertField.CreatedOn

  override protected def defaultOrderingCondition: OrderingCondition = OrderingCondition.DescendingOrder

  override protected def parseField(unsafeField: String): Option[FixedPriceAlertField] = {
    FixedPriceAlertField.withNameOption(unsafeField)
  }
}
