package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.AlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.AlertPostgresDAO
import com.alexitc.coinalerts.errors.{AlertNotFound, InvalidPriceError, UnknownAlertTypeError}
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}
import play.api.db.Database

class AlertPostgresDataHandler @Inject() (
    protected val database: Database,
    alertPostgresDAO: AlertPostgresDAO)
    extends AlertBlockingDataHandler
    with AnormPostgresDAL{

  override def create(createAlertModel: CreateAlertModel, userId: UserId): ApplicationResult[Alert] = withConnection { implicit conn =>
    createAlertModel.alertType match {
      case AlertType.UNKNOWN(_) =>
        Bad(UnknownAlertTypeError).accumulating

      case _ =>
        val result = alertPostgresDAO.create(createAlertModel, userId)
        Good(result)
    }
  }

  override def markAsTriggered(alertId: AlertId): ApplicationResult[Unit] = withConnection { implicit conn =>
    val updatedRows = alertPostgresDAO.markAsTriggered(alertId)
    if (updatedRows == 1) {
      Good(())
    } else {
      Bad(AlertNotFound).accumulating
    }
  }

  override def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): ApplicationResult[List[Alert]] = withConnection { implicit conn =>
    if (currentPrice <= 0) {
      Bad(InvalidPriceError).accumulating
    } else {
      val alertList = alertPostgresDAO.findPendingAlertsForPrice(market, book, currentPrice)
      Good(alertList)
    }
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[Alert]] = withConnection { implicit conn =>
    val alerts = alertPostgresDAO.getAlerts(userId, query)
    val total = alertPostgresDAO.countAlerts(userId)
    val result = PaginatedResult(query.offset, query.limit, total, alerts)
    Good(result)
  }
}
