package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.{Alert, CreateAlertModel, UserId}

trait AlertDataHandler {

  def create(createAlertModel: CreateAlertModel, userId: UserId): ApplicationResult[Alert]

}
