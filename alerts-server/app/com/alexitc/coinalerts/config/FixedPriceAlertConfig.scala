package com.alexitc.coinalerts.config

import javax.inject.Inject

import com.alexitc.playsonify.models.Count
import play.api.Configuration

trait FixedPriceAlertConfig {

  def maximumNumberOfAlertsPerUser: Count
}

class PlayFixedPriceAlertConfig @Inject() (config: Configuration) extends FixedPriceAlertConfig {

  private val defaultMaximumNumberOfAlertsPerUser = Count(15)

  override def maximumNumberOfAlertsPerUser: Count = {
    val intMaybe = config.getOptional[Int]("fixedPriceAlert.maximumNumberOfAlertsPerUser")

    intMaybe
        .filter(_ > 0)
        .map(Count.apply)
        .getOrElse(defaultMaximumNumberOfAlertsPerUser)
  }
}
