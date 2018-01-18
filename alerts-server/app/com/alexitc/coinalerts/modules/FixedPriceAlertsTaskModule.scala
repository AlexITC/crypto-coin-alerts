package com.alexitc.coinalerts.modules

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.FixedPriceAlertsTaskConfig
import com.alexitc.coinalerts.core.ShutdownHandler
import com.alexitc.coinalerts.tasks.{FixedPriceAlertsTask, ShutdownableTaskRunner}
import play.api.inject.{SimpleModule, _}

import scala.concurrent.duration.FiniteDuration

class FixedPriceAlertsTaskModule
    extends SimpleModule(bind[FixedPriceAlertsTaskRunner].toSelf.eagerly())

/**
 * Runs the fixed price alerts task frequently.
 */
@Singleton
class FixedPriceAlertsTaskRunner @Inject() (
    protected val shutdownHandler: ShutdownHandler,
    protected val actorSystem: ActorSystem,
    config: FixedPriceAlertsTaskConfig,
    alertsTask: FixedPriceAlertsTask)
    extends ShutdownableTaskRunner{

  override protected def initialDelay: FiniteDuration = config.initialDelay

  override protected def interval: FiniteDuration = config.interval

  override protected def run() = {
    alertsTask.execute()
  }

  register()

}
