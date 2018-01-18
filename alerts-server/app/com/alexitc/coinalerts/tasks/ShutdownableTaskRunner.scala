package com.alexitc.coinalerts.tasks

import akka.actor.ActorSystem
import com.alexitc.coinalerts.core.ShutdownHandler
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal

trait ShutdownableTaskRunner {

  private val logger = LoggerFactory.getLogger(this.getClass)

  protected def shutdownHandler: ShutdownHandler

  protected def actorSystem: ActorSystem

  protected def initialDelay: FiniteDuration

  protected def interval: FiniteDuration

  protected def run(): Future[Unit]

  @volatile
  private var running = false

  protected def register() = {
    logger.info("Registering task")

    shutdownHandler.addShutdownHook(() => Future {

      if (running) {
        logger.info("Waiting for task to complete")
      }

      // if we are shutting down, do we care no making a mess with threads? I hope we don't
      scala.concurrent.blocking {
        while (running) {
          Thread.sleep(1000)
        }
      }
    })

    val _ = actorSystem.scheduler.schedule(
      initialDelay = initialDelay,
      interval = interval) { execute() }
  }

  private def execute() = {
    if (!shutdownHandler.isShuttingDown) {
      running = true

      logger.info("Running task")

      run()
          .recover {
            case NonFatal(ex) =>
              logger.error("Failed to run task", ex)
          }
          .foreach { _ =>
            running = false
          }
    }
  }
}
