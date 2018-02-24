package com.alexitc.coinalerts.commons.examples

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonControllerComponents, PublicErrorRenderer}
import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

class CustomControllerComponents @Inject()(
    override val messagesControllerComponents: MessagesControllerComponents,
    override val executionContext: ExecutionContext,
    override val publicErrorRenderer: PublicErrorRenderer,
    override val applicationErrorMapper: CustomErrorMapper,
    override val authenticatorService: CustomAuthenticator)
    extends JsonControllerComponents[CustomUser]
