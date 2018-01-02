package com.alexitc.coinalerts.modules

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.alexitc.coinalerts.config.ExchangeCurrencySeederTaskConfig
import com.alexitc.coinalerts.tasks.ExchangeCurrencySeederTask
import org.slf4j.LoggerFactory
import play.api.inject.{SimpleModule, bind}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class ExchangeCurrencySeederTaskModule
    extends SimpleModule(bind[ExchangeCurrencySeederTaskRunner].toSelf.eagerly())

/**
 * Runs the alert task frequently.
 */
@Singleton
class ExchangeCurrencySeederTaskRunner @Inject() (
    actorSystem: ActorSystem,
    config: ExchangeCurrencySeederTaskConfig,
    exchangeCurrencySeederTask: ExchangeCurrencySeederTask)(
    implicit executionContext: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  start()

  // TODO: Add shutdown hook?
  def start() = {
    logger.info("Starting currency seeder runner...")

    val _ = actorSystem.scheduler.schedule(
      initialDelay = config.initialDelay,
      interval = config.interval) { runTask() }
  }

  def runTask(): Unit = {
    logger.info("Running currency seeder task...")
    val _ = exchangeCurrencySeederTask.execute()
        .recover {
          case NonFatal(ex) =>
            logger.error("Unexpected error while running currency seeder task", ex)
        }
  }
}
