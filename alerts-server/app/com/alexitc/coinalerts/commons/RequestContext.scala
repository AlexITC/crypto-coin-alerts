package com.alexitc.coinalerts.commons

import play.api.i18n.Lang

case class RequestContext[T](model: T, lang: Lang)
