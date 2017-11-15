package com.alexitc.coinalerts.modules

import javax.inject.Inject

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.AlertTaskConfig
import org.slf4j.LoggerFactory
import play.api.inject.{SimpleModule, _}

import scala.concurrent.ExecutionContext

class AlertTaskModule
    extends SimpleModule(bind[AlertTaskRunner].toSelf.eagerly())

/**
 * Runs the alert task frequently.
 */
class AlertTaskRunner @Inject() (
    actorSystem: ActorSystem,
    config: AlertTaskConfig)(
    implicit executionContext: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def start() = {
    if (config.enabled) {
      logger.info("Starting alert task runner...")

      actorSystem.scheduler.schedule(
        initialDelay = config.initialDelay,
        interval = config.interval,
      )(runTask)
    } else {
      logger.info("Alert task is disabled")
    }
  }


  def runTask = {
    logger.info("Running alert task...")
  }
}
