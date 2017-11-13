package com.alexitc.coinalerts.config

import javax.inject._

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "database.dispatcher")
