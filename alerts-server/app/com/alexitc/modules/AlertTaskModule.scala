package com.alexitc.modules

import javax.inject.Inject

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory
import play.api.inject.{SimpleModule, _}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AlertTaskModule
    extends SimpleModule(bind[AlertTaskRunner].toSelf.eagerly())

/**
 * Runs the alert task frequently.
 *
 * @param actorSystem
 * @param executionContext
 */
class AlertTaskRunner @Inject() (
    actorSystem: ActorSystem)(
    implicit executionContext: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting alert task runner...")

  // TODO: Read values from config
  actorSystem.scheduler.schedule(
    initialDelay = 30.seconds,
    interval = 5.minutes,
  )(runTask)

  def runTask = {
    logger.info("Delivering alerts...")
  }
}
