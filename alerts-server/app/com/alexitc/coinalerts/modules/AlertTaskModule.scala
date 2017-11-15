package com.alexitc.coinalerts.modules

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.AlertTaskConfig
import com.alexitc.coinalerts.tasks.AlertsTask
import org.slf4j.LoggerFactory
import play.api.inject.{SimpleModule, _}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class AlertTaskModule
    extends SimpleModule(bind[AlertTaskRunner].toSelf.eagerly())

/**
 * Runs the alert task frequently.
 */
@Singleton
class AlertTaskRunner @Inject() (
    actorSystem: ActorSystem,
    config: AlertTaskConfig,
    alertsTask: AlertsTask)(
    implicit executionContext: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  start()

  // TODO: Add shutdown hook?
  def start() = {
    if (config.enabled) {
      logger.info("Starting alert task runner...")

      val _ = actorSystem.scheduler.schedule(
        initialDelay = config.initialDelay,
        interval = config.interval) { runTask() }
    } else {
      logger.info("Alert task is disabled")
    }
  }

  def runTask(): Unit = {
    logger.info("Running alert task...")
    val _ = alertsTask.execute()
        .recover {
          case NonFatal(ex) =>
            logger.error("Unexpected error while running TriggerAlertsTask", ex)
        }
  }
}
