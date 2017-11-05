package com.alexitc.coinalerts.config

import javax.inject.Inject

import play.api.Configuration

trait JWTConfig {

  def secretKey: JWTSecretKey
}

case class JWTSecretKey(string: String) extends AnyVal

class PlayJWTConfig @Inject() (configuration: Configuration) extends JWTConfig {

  override def secretKey: JWTSecretKey = {
    val string = configuration.get[String]("jwt.secret")
    JWTSecretKey(string)
  }
}
