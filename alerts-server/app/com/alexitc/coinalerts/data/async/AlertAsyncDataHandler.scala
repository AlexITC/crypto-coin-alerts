package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.AlertDataHandler
import com.alexitc.coinalerts.models.{Alert, CreateAlertModel, UserId}

import scala.concurrent.Future

class AlertAsyncDataHandler @Inject() (
    alertDataHandler: AlertDataHandler)(
    implicit ec: DatabaseExecutionContext) {

  def create(createAlertModel: CreateAlertModel, userId: UserId): FutureApplicationResult[Alert] = Future {
    alertDataHandler.create(createAlertModel, userId)
  }
}
