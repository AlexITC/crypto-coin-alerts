package com.alexitc.coinalerts.models

object FixedPriceAlertFilter {

  sealed trait Condition

  sealed trait UserCondition extends Condition
  case object AnyUserCondition extends UserCondition
  case class JustThisUserCondition(userId: UserId) extends UserCondition

  sealed trait TriggeredCondition extends Condition
  case object AnyTriggeredCondition extends TriggeredCondition
  case object HasBeenTriggeredCondition extends TriggeredCondition
  case object HasNotBeenTriggeredCondition extends TriggeredCondition

  case class Conditions(triggered: TriggeredCondition, user: UserCondition)
}
