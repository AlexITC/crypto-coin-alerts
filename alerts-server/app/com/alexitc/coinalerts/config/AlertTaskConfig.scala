package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

import scala.concurrent.duration.{DurationLong, FiniteDuration}

trait AlertTaskConfig {

  def initialDelay: FiniteDuration

  def interval: FiniteDuration
}

class PlayAlertTaskConfig @Inject() (configuration: Configuration) extends AlertTaskConfig {

  override def initialDelay: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("alertTask.initialDelay").getOrElse(1.minute)
  }

  override def interval: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("alertTask.interval").getOrElse(5.minutes)
  }
}
