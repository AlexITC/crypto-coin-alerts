package com.alexitc.coinalerts.commons

import play.api.i18n.Lang

sealed trait RequestContext {
  def lang: Lang
}

sealed trait HasModel[T] {
  def model: T
}

final case class PublicRequestContext(lang: Lang) extends RequestContext
final case class PublicRequestContextWithModel[T](model: T, lang: Lang)
    extends RequestContext with HasModel[T]

final case class AuthenticatedRequestContext[A](auth: A, lang: Lang) extends RequestContext
final case class AuthenticatedRequestContextWithModel[A, T](auth: A, model: T, lang: Lang)
    extends RequestContext with HasModel[T]
