package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.config.{DatabaseExecutionContext, ExternalServiceExecutionContext, TaskExecutionContext}

import scala.concurrent.ExecutionContext

object ExecutionContexts {

  implicit val globalEC: ExecutionContext = scala.concurrent.ExecutionContext.global

  implicit val databaseEC: DatabaseExecutionContext = new DatabaseExecutionContext {
    override def execute(runnable: Runnable): Unit = globalEC.execute(runnable)

    override def reportFailure(cause: Throwable): Unit = globalEC.reportFailure(cause)
  }

  implicit val taskEC: TaskExecutionContext = new TaskExecutionContext {
    override def execute(runnable: Runnable): Unit = globalEC.execute(runnable)

    override def reportFailure(cause: Throwable): Unit = globalEC.reportFailure(cause)
  }

  implicit val externalServiceEC: ExternalServiceExecutionContext = new ExternalServiceExecutionContext {
    override def execute(runnable: Runnable): Unit = globalEC.execute(runnable)

    override def reportFailure(cause: Throwable): Unit = globalEC.reportFailure(cause)
  }

}
