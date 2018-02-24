package com.alexitc.coinalerts.commons

import java.util.UUID

case class ErrorId(string: String) extends AnyVal

object ErrorId {
  def create: ErrorId = ErrorId(UUID.randomUUID().toString.replace("-", ""))
}
