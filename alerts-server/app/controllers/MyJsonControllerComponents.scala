package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonControllerComponents, PublicErrorRenderer}
import com.alexitc.coinalerts.errors.MyApplicationErrorMapper
import com.alexitc.coinalerts.models.UserId
import com.alexitc.coinalerts.services.JWTAuthenticatorService
import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

class MyJsonControllerComponents @Inject() (
    override val messagesControllerComponents: MessagesControllerComponents,
    override val executionContext: ExecutionContext,
    override val publicErrorRenderer: PublicErrorRenderer,
    override val applicationErrorMapper: MyApplicationErrorMapper,
    override val authenticatorService: JWTAuthenticatorService)
    extends JsonControllerComponents[UserId]
