package com.alexitc.coinalerts.models

import play.api.i18n.Lang
import play.api.libs.json.{Json, Reads, Writes}

case class UserPreferences (userId: UserId, lang: Lang)

object UserPreferences {

  val AvailableLangs = List("en").map(Lang.apply)

  def default(userId: UserId): UserPreferences = {
    val defaultPreferences = SetUserPreferencesModel.default

    UserPreferences(userId, defaultPreferences.lang)
  }

  def from(userId: UserId, preferencesModel: SetUserPreferencesModel): UserPreferences = {
    UserPreferences(userId, preferencesModel.lang)
  }

  implicit val writes: Writes[UserPreferences] = Json.writes[UserPreferences]
}

case class SetUserPreferencesModel(lang: Lang)
object SetUserPreferencesModel {

  private val DefaultLang = Lang("en")

  val default: SetUserPreferencesModel = {
    SetUserPreferencesModel(DefaultLang)
  }

  implicit val reads: Reads[SetUserPreferencesModel] = Json.reads[SetUserPreferencesModel]
}
