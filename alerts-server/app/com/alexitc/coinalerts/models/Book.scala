package com.alexitc.coinalerts.models

import play.api.libs.json._

case class Book(major: String, minor: String) {
  val string = s"${major}_$minor".toUpperCase
}
object Book {

  def fromString(string: String): Option[Book] = {
    Option(string.toUpperCase.split("_"))
        .filter(_.length == 2)
        .map { parts =>
          val major = parts(0)
          val minor = parts(1)
          Book(major, minor)
        }
  }

  implicit val reads: Reads[Book] = {
    JsPath.read[String].collect(JsonValidationError("error.book.invalid")) {
      case string if fromString(string).isDefined => fromString(string).get
    }
  }

  implicit val writes: Writes[Book] = Writes[Book] { book => JsString(book.string) }
}
