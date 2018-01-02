package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

import scala.concurrent.duration.{DurationDouble, FiniteDuration}

trait ExchangeCurrencySeederTaskConfig {

  def initialDelay: FiniteDuration

  def interval: FiniteDuration
}

class PlayExchangeCurrencySeederTaskConfig @Inject() (
    configuration: Configuration)
    extends ExchangeCurrencySeederTaskConfig {

  override def initialDelay: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("currencySeederTask.initialDelay").getOrElse(15.seconds)
  }

  override def interval: FiniteDuration = {
    configuration.getOptional[FiniteDuration]("currencySeederTask.interval").getOrElse(1.day)
  }
}
