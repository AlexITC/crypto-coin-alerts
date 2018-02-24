package com.alexitc.coinalerts.commons

import play.api.mvc.MessagesControllerComponents

import scala.concurrent.ExecutionContext

trait JsonControllerComponents[A] {

  def messagesControllerComponents: MessagesControllerComponents

  def executionContext: ExecutionContext

  def publicErrorRenderer: PublicErrorRenderer

  def applicationErrorMapper: ApplicationErrorMapper

  def authenticatorService: AbstractAuthenticatorService[A]
}
