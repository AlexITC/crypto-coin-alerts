package com.alexitc.coinalerts.services

import javax.inject.Inject

import com.alexitc.coinalerts.data.async.NewCurrencyAlertFutureDataHandler
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, UserId}
import com.alexitc.playsonify.core.FutureApplicationResult

class NewCurrencyAlertService @Inject()(newCurrencyAlertFutureDataHandler: NewCurrencyAlertFutureDataHandler) {

  def create(userId: UserId, exchange: Exchange): FutureApplicationResult[NewCurrencyAlert] = {
    newCurrencyAlertFutureDataHandler.create(userId, exchange)
  }

  def get(userId: UserId): FutureApplicationResult[List[NewCurrencyAlert]] = {
    newCurrencyAlertFutureDataHandler.get(userId)
  }

  def delete(userId: UserId, exchange: Exchange): FutureApplicationResult[NewCurrencyAlert] = {
    newCurrencyAlertFutureDataHandler.delete(userId, exchange)
  }
}
