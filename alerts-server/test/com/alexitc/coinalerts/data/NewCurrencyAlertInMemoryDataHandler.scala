package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.{NewCurrencyAlertNotFoundError, RepeatedExchangeError}
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, NewCurrencyAlertId, UserId}
import org.scalactic.{Bad, Good}

class NewCurrencyAlertInMemoryDataHandler extends NewCurrencyAlertBlockingDataHandler {

  private val alerts = new scala.collection.mutable.ListBuffer[NewCurrencyAlert]()

  private def withLock[A](f: => A) = alerts.synchronized(f)

  override def create(userId: UserId, exchange: Exchange): ApplicationResult[NewCurrencyAlert] = withLock {
    val exists = alerts.exists { alert =>
      alert.userId == userId && alert.exchange == exchange
    }

    if (exists) {
      Bad(RepeatedExchangeError).accumulating
    } else {
      val alert = NewCurrencyAlert(NewCurrencyAlertId(alerts.size), userId, exchange)

      alerts += alert

      Good(alert)
    }
  }

  override def get(userId: UserId): ApplicationResult[List[NewCurrencyAlert]] = withLock {
    val list = alerts.toList.filter(_.userId == userId)

    Good(list)
  }

  override def getAll(): ApplicationResult[List[NewCurrencyAlert]] = withLock {
    val list = alerts.toList

    Good(list)
  }

  override def delete(id: NewCurrencyAlertId, userId: UserId): ApplicationResult[NewCurrencyAlert] = withLock {
    alerts
        .find { alert =>
          alert.userId == userId && alert.id == id
        }
        .map { alert =>
          alerts -= alert

          Good(alert)
        }
        .getOrElse {
          Bad(NewCurrencyAlertNotFoundError).accumulating
        }
  }
}
