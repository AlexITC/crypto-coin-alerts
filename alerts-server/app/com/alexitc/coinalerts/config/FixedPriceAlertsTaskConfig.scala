package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

import scala.concurrent.duration.{DurationLong, FiniteDuration}

trait FixedPriceAlertsTaskConfig {

  def initialDelay: FiniteDuration

  def interval: FiniteDuration
}

class PlayFixedPriceAlertsTaskConfig @Inject()(configuration: Configuration) extends FixedPriceAlertsTaskConfig {

  override def initialDelay: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("fixedPriceAlertsTask.initialDelay").getOrElse(1.minute)
  }

  override def interval: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("fixedPriceAlertsTask.interval").getOrElse(5.minutes)
  }
}
