package com.alexitc.coinalerts.core

case class ErrorId(string: String) extends AnyVal
object ErrorId {
  def create: ErrorId = ErrorId(RandomIdGenerator.stringId)
}
