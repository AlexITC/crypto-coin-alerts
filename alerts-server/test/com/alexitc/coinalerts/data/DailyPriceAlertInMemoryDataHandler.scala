package com.alexitc.coinalerts.data
import java.time.OffsetDateTime

import com.alexitc.coinalerts.commons.{ApplicationResult, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
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

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[DailyPriceAlert]] = {
    val userAlertList = alertList.toList.filter(_.userId == userId)

    val result = PaginatedResult(
      total = Count(userAlertList.length),
      offset = query.offset,
      limit = query.limit,
      data = userAlertList.slice(query.offset.int, query.offset.int + query.limit.int))

    Good(result)
  }
}
