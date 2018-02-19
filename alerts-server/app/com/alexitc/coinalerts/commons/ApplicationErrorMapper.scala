package com.alexitc.coinalerts.commons

import play.api.i18n.Lang

trait ApplicationErrorMapper {

  def toPublicErrorList(error: ApplicationError)(implicit lang: Lang): Seq[PublicError]
}
