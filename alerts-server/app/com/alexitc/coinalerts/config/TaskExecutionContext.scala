package com.alexitc.coinalerts.config

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

@Singleton
class TaskExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "task.dispatcher")
