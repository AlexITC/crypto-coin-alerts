package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.data.NewCurrencyAlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.NewCurrencyAlertPostgresDAO
import com.alexitc.coinalerts.errors.{
  NewCurrencyAlertNotFoundError,
  PostgresIntegrityViolationError,
  RepeatedExchangeError,
  VerifiedUserNotFound
}
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, UserId}
import com.alexitc.playsonify.core.ApplicationResult
import org.scalactic.{Good, One, Or}
import play.api.db.Database

class NewCurrencyAlertPostgresDataHandler @Inject()(
    protected val database: Database,
    newCurrencyAlertDAO: NewCurrencyAlertPostgresDAO)
    extends NewCurrencyAlertBlockingDataHandler
    with AnormPostgresDAL {

  override def create(userId: UserId, exchange: Exchange): ApplicationResult[NewCurrencyAlert] = {
    val result = withConnection { implicit conn =>
      val maybe = newCurrencyAlertDAO.create(userId, exchange)

      Or.from(maybe, One(RepeatedExchangeError))
    }

    result.badMap { errors =>
      errors.map {
        case PostgresIntegrityViolationError(Some("user_id"), _) => VerifiedUserNotFound
        case e => e
      }
    }
  }

  override def get(userId: UserId): ApplicationResult[List[NewCurrencyAlert]] = withConnection { implicit conn =>
    val list = newCurrencyAlertDAO.get(userId)

    Good(list)
  }

  override def getBy(exchange: Exchange): ApplicationResult[List[NewCurrencyAlert]] = withConnection { implicit conn =>
    val list = newCurrencyAlertDAO.getBy(exchange)

    Good(list)
  }

  override def getAll(): ApplicationResult[List[NewCurrencyAlert]] = withConnection { implicit conn =>
    val list = newCurrencyAlertDAO.getAll

    Good(list)
  }

  override def delete(userId: UserId, exchange: Exchange): ApplicationResult[NewCurrencyAlert] = withConnection {
    implicit conn =>
      val maybe = newCurrencyAlertDAO.delete(userId, exchange)

      Or.from(maybe, One(NewCurrencyAlertNotFoundError))
  }
}
