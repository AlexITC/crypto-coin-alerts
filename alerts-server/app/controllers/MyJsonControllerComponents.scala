package controllers

import javax.inject.Inject

import com.alexitc.coinalerts.commons.{JsonControllerComponents, PublicErrorRenderer}
import com.alexitc.coinalerts.errors.MyApplicationErrorMapper
import com.alexitc.coinalerts.services.JWTService
import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

class MyJsonControllerComponents @Inject() (
    override val messagesControllerComponents: MessagesControllerComponents,
    override val jwtService: JWTService,
    override val executionContext: ExecutionContext,
    override val publicErrorRenderer: PublicErrorRenderer,
    override val applicationErrorMapper: MyApplicationErrorMapper)
    extends JsonControllerComponents
