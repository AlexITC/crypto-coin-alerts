package com.alexitc.coinalerts.models

import play.api.i18n.Lang

case class UserPreferences (userId: UserId, lang: Lang)

object UserPreferences {

  private val DefaultLang = Lang("en")

  def default(userId: UserId): UserPreferences = {
    UserPreferences(userId, DefaultLang)
  }
}
