package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, NewCurrencyAlertId, UserId}

import scala.language.higherKinds

trait NewCurrencyAlertDataHandler[F[_]] {

  def create(userId: UserId, exchange: Exchange): F[NewCurrencyAlert]

  def get(userId: UserId): F[List[NewCurrencyAlert]]

  def getBy(exchange: Exchange): F[List[NewCurrencyAlert]]

  def getAll(): F[List[NewCurrencyAlert]]

  def delete(id: NewCurrencyAlertId, userId: UserId): F[NewCurrencyAlert]

}

trait NewCurrencyAlertBlockingDataHandler extends NewCurrencyAlertDataHandler[ApplicationResult]
