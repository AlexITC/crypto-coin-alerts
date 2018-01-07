package com.alexitc.coinalerts.config

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.ExecutionContext

trait TaskExecutionContext extends ExecutionContext

@Singleton
class TaskAkkaExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "task.dispatcher")
    with TaskExecutionContext
