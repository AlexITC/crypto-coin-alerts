package com.alexitc.coinalerts.core

import javax.inject.{Inject, Singleton}

import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle
import sun.misc.Signal

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

@Singleton
class ShutdownHandler @Inject() (application: ApplicationLifecycle) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @volatile
  private var shuttingDown = false

  private val shutdownHooks = mutable.ListBuffer[() => Future[Unit]]()

  Signal.handle(new Signal("TERM"), onSIGTERM)

  def addShutdownHook(hook: () => Future[Unit]): Unit = shutdownHooks.synchronized {
    val _ = shutdownHooks += hook
  }

  def isShuttingDown: Boolean = shuttingDown

  private def onSIGTERM(signal: Signal): Unit = {
    shuttingDown = true

    callShutdownHooks()
    shutdownPlay()

    // wait a bit to let tasks and requests to complete
    Thread.sleep(3000)
    shutdownApp()
  }

  private def callShutdownHooks() = {
    logger.info("Calling shutdown hooks")

    val result = shutdownHooks.foldLeft(Future.unit) { (tmp, hook) =>
      val f = hook().recover {
        case NonFatal(ex) =>
          logger.error("Shutdown hook failed", ex)
      }

      tmp.flatMap { _ => f }
    }

    // wait for hooks to complete
    Await.result(result, 1.minute)
  }

  private def shutdownPlay() = {
    val result = application.stop()

    Await.result(result, 1.minute)
  }

  private def shutdownApp() = {
    sys.exit()
  }
}
