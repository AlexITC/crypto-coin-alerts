package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

trait MailgunConfig {
  def apiSecretKey: MailgunAPISecretKey
  def from: MailgunFrom
  def domain: MailgunDomain
}

case class MailgunAPISecretKey(string: String) extends AnyVal
case class MailgunFrom(string: String) extends AnyVal
case class MailgunDomain(string: String) extends AnyVal

class PlayMailgunConfig @Inject() (configuration: Configuration) extends MailgunConfig {
  override def apiSecretKey: MailgunAPISecretKey = {
    val string = configuration.get[String]("mailgun.apiSecretKey")
    MailgunAPISecretKey(string)
  }

  override def from: MailgunFrom = {
    val string = configuration.get[String]("mailgun.from")
    MailgunFrom(string)
  }

  override def domain: MailgunDomain = {
    val string = configuration.get[String]("mailgun.domain")
    MailgunDomain(string)
  }
}
