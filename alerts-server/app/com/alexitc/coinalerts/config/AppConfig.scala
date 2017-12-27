package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

trait AppConfig {

  def url: AppURL
}

case class AppURL(string: String) extends AnyVal {
  def concat(other: String): AppURL = {
    val prefix = removeTrailingSlash(string)
    val suffix = removeLeadingSlash(other)

    AppURL(s"$prefix/$suffix")
  }

  private def removeLeadingSlash(string: String) = {
    if (string.startsWith("/")) {
      string.substring(1)
    } else {
      string
    }
  }

  private def removeTrailingSlash(string: String) = {
    if (string.endsWith("/")) {
      string.substring(0, string.length - 1)
    } else {
      string
    }
  }
}

class PlayAppConfig @Inject() (config: Configuration) extends AppConfig {

  override def url: AppURL = {
    val string = config.get[String]("app.url")
    AppURL(string)
  }
}
