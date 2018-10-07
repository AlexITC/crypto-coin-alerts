package com.alexitc.coinalerts.modules

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.ExchangeCurrencySeederTaskConfig
import com.alexitc.coinalerts.core.ShutdownHandler
import com.alexitc.coinalerts.tasks.{ExchangeCurrencySeederTask, ShutdownableTaskRunner}
import play.api.inject.{SimpleModule, bind}

import scala.concurrent.duration.FiniteDuration

class ExchangeCurrencySeederTaskModule extends SimpleModule(bind[ExchangeCurrencySeederTaskRunner].toSelf.eagerly())

@Singleton
class ExchangeCurrencySeederTaskRunner @Inject()(
    protected val shutdownHandler: ShutdownHandler,
    protected val actorSystem: ActorSystem,
    config: ExchangeCurrencySeederTaskConfig,
    exchangeCurrencySeederTask: ExchangeCurrencySeederTask)
    extends ShutdownableTaskRunner {

  override protected def initialDelay: FiniteDuration = config.initialDelay

  override protected def interval: FiniteDuration = config.interval

  override protected def run() = {
    exchangeCurrencySeederTask.execute()
  }

  register()
}
