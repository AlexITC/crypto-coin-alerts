package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.DailyPriceAlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.DailyPriceAlertDAO
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, DailyPriceAlert, UserId}
import play.api.db.Database

class DailyPriceAlertPostgresDataHandler @Inject() (
    protected val database: Database,
    dailyPriceAlertDAO: DailyPriceAlertDAO)
    extends DailyPriceAlertBlockingDataHandler
        with AnormPostgresDAL {

  override def create(
      userId: UserId,
      createDailyPriceAlert: CreateDailyPriceAlertModel): ApplicationResult[DailyPriceAlert] = withConnection { implicit conn =>

    dailyPriceAlertDAO.create(userId, createDailyPriceAlert)
  }
}
