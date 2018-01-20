package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.commons.FutureApplicationResult
import com.alexitc.coinalerts.data.async.NewCurrencyAlertFutureDataHandler
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, NewCurrencyAlertId, UserId}

class NewCurrencyAlertService @Inject() (newCurrencyAlertFutureDataHandler: NewCurrencyAlertFutureDataHandler) {

  def create(userId: UserId, exchange: Exchange): FutureApplicationResult[NewCurrencyAlert] = {
    newCurrencyAlertFutureDataHandler.create(userId, exchange)
  }

  def get(userId: UserId): FutureApplicationResult[List[NewCurrencyAlert]] = {
    newCurrencyAlertFutureDataHandler.get(userId)
  }

  def delete(id: NewCurrencyAlertId, userId: UserId): FutureApplicationResult[NewCurrencyAlert] = {
    newCurrencyAlertFutureDataHandler.delete(id, userId)
  }
}
