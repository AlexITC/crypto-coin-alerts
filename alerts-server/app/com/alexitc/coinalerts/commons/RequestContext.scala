package com.alexitc.coinalerts.commons

import com.alexitc.coinalerts.models.UserId
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

final case class AuthenticatedRequestContext(userId: UserId, lang: Lang) extends RequestContext
final case class AuthenticatedRequestContextWithModel[T](userId: UserId, model: T, lang: Lang)
    extends RequestContext with HasModel[T]
