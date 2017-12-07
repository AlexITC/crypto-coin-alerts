package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.DailyPriceAlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.DailyPriceAlertPostgresDAO
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import org.scalactic.Good
import play.api.db.Database

class DailyPriceAlertPostgresDataHandler @Inject() (
    protected val database: Database,
    dailyPriceAlertDAO: DailyPriceAlertPostgresDAO)
    extends DailyPriceAlertBlockingDataHandler
        with AnormPostgresDAL {

  override def create(
      userId: UserId,
      createDailyPriceAlert: CreateDailyPriceAlertModel): ApplicationResult[DailyPriceAlert] = withConnection { implicit conn =>

    dailyPriceAlertDAO.create(userId, createDailyPriceAlert)
  }

  override def getAlerts(
      userId: UserId,
      query: PaginatedQuery): ApplicationResult[PaginatedResult[DailyPriceAlert]] = withConnection { implicit conn =>

    val alerts = dailyPriceAlertDAO.getAlerts(userId, query)
    val total = dailyPriceAlertDAO.countAlerts(userId)
    val result = PaginatedResult(query.offset, query.limit, total, alerts)
    Good(result)
  }
}
