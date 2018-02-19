package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.services.JWTService
import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

trait JsonControllerComponents {

  def messagesControllerComponents: MessagesControllerComponents

  // TODO: allow to override it
  def jwtService: JWTService

  def executionContext: ExecutionContext

  def publicErrorRenderer: PublicErrorRenderer

  def applicationErrorMapper: ApplicationErrorMapper

}
