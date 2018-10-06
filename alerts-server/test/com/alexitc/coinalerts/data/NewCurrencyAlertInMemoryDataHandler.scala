package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.errors.{NewCurrencyAlertNotFoundError, RepeatedExchangeError}
import com.alexitc.coinalerts.models.NewCurrencyAlert.NewCurrencyAlertId
import com.alexitc.coinalerts.models.{Exchange, NewCurrencyAlert, UserId}
import com.alexitc.playsonify.core.ApplicationResult
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

  override def getBy(exchange: Exchange): ApplicationResult[List[NewCurrencyAlert]] = withLock {
    val list = alerts.toList.filter(_.exchange == exchange)

    Good(list)
  }

  override def getAll(): ApplicationResult[List[NewCurrencyAlert]] = withLock {
    val list = alerts.toList

    Good(list)
  }

  override def delete(userId: UserId, exchange: Exchange): ApplicationResult[NewCurrencyAlert] = withLock {
    alerts
        .find { alert =>
          alert.userId == userId && alert.exchange == exchange
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
