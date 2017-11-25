package com.alexitc.coinalerts.core

import com.alexitc.coinalerts.models.RandomIdGenerator

case class ErrorId(string: String) extends AnyVal
object ErrorId {
  def create: ErrorId = ErrorId(RandomIdGenerator.stringId)
}
