package com.alexitc.coinalerts.config

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.ExecutionContext

trait ExternalServiceExecutionContext extends ExecutionContext

@Singleton
class ExternalServiceAkkaExecutionContext @Inject()(system: ActorSystem)
    extends CustomExecutionContext(system, "externalService.dispatcher")
    with ExternalServiceExecutionContext
