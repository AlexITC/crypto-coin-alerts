package com.alexitc.coinalerts.data.async

import javax.inject.Inject

import com.alexitc.coinalerts.config.DatabaseExecutionContext
import com.alexitc.coinalerts.data.{NewCurrencyAlertBlockingDataHandler, NewCurrencyAlertDataHandler}
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, UserId}
import com.alexitc.playsonify.core.FutureApplicationResult

import scala.concurrent.Future

class NewCurrencyAlertFutureDataHandler @Inject()(blockingDataHandler: NewCurrencyAlertBlockingDataHandler)(
    implicit ec: DatabaseExecutionContext)
    extends NewCurrencyAlertDataHandler[FutureApplicationResult] {

  override def create(userId: UserId, exchange: Exchange): FutureApplicationResult[NewCurrencyAlert] = Future {
    blockingDataHandler.create(userId, exchange)
  }

  override def get(userId: UserId): FutureApplicationResult[List[NewCurrencyAlert]] = Future {
    blockingDataHandler.get(userId)
  }

  override def getBy(exchange: Exchange): FutureApplicationResult[List[NewCurrencyAlert]] = Future {
    blockingDataHandler.getBy(exchange)
  }

  override def getAll(): FutureApplicationResult[List[NewCurrencyAlert]] = Future {
    blockingDataHandler.getAll()
  }

  override def delete(userId: UserId, exchange: Exchange): FutureApplicationResult[NewCurrencyAlert] = Future {
    blockingDataHandler.delete(userId, exchange)
  }
}
