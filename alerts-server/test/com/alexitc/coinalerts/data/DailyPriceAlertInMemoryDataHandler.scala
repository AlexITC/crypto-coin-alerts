package com.alexitc.coinalerts.data
import java.time.OffsetDateTime

import com.alexitc.coinalerts.commons.{ApplicationResult, RandomDataGenerator}
import com.alexitc.coinalerts.errors.RepeatedDailyPriceAlertError
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import org.scalactic.{Bad, Good}

import scala.collection.mutable

class DailyPriceAlertInMemoryDataHandler extends DailyPriceAlertBlockingDataHandler  {

  private val alertList = mutable.ListBuffer[DailyPriceAlert]()

  override def create(userId: UserId, createDailyPriceAlert: CreateDailyPriceAlertModel): ApplicationResult[DailyPriceAlert] = {
    val alert = DailyPriceAlert(RandomDataGenerator.dailyPriceAlertId, userId, createDailyPriceAlert.market, createDailyPriceAlert.book, OffsetDateTime.now())
    val exists = alertList.toList.exists { existingAlert =>
      existingAlert.userId == userId &&
      existingAlert.market == alert.market &&
      existingAlert.book == alert.book
    }

    if (exists) {
      Bad(RepeatedDailyPriceAlertError).accumulating
    } else {
      alertList += alert
      Good(alert)
    }
  }
}
