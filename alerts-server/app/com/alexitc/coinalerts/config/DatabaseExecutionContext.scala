package com.alexitc.coinalerts.config

import javax.inject._

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.ExecutionContext

trait DatabaseExecutionContext extends ExecutionContext

@Singleton
class DatabaseAkkaExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "database.dispatcher")
    with DatabaseExecutionContext
