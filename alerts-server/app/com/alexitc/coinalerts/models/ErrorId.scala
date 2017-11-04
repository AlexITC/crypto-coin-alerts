package com.alexitc.coinalerts.models

case class ErrorId(string: String) extends AnyVal
object ErrorId {
  def create: ErrorId = ErrorId(RandomIdGenerator.stringId)
}
