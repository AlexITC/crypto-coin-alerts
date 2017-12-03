package com.alexitc.coinalerts.commons

import javax.inject.Inject

import com.alexitc.coinalerts.commons.actions.LoggingAction
import com.alexitc.coinalerts.errors.JsonErrorRenderer
import com.alexitc.coinalerts.services.JWTService
import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

class JsonControllerComponents @Inject() (
    val messagesControllerComponents: MessagesControllerComponents,
    val loggingAction: LoggingAction,
    val jwtService: JWTService,
    val errorRenderer: JsonErrorRenderer,
    val executionContext: ExecutionContext)
