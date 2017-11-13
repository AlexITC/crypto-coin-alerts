package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.data.AlertDataHandler
import com.alexitc.coinalerts.data.anorm.dao.AlertPostgresDAO
import com.alexitc.coinalerts.errors.{AlertNotFound, BasePriceRequiredError, InvalidPriceError, UnknownAlertTypeError}
import com.alexitc.coinalerts.models.AlertType.{BASE_PRICE, DEFAULT}
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}
import play.api.db.Database

class AlertPostgresDataHandler @Inject() (
    protected val database: Database,
    alertPostgresDAO: AlertPostgresDAO)
    extends AlertDataHandler
    with AnormPostgresDAL{

  def create(createAlertModel: CreateAlertModel, userId: UserId): ApplicationResult[Alert] = withConnection { implicit conn =>
    createAlertModel.alertType match {
      case DEFAULT =>
        val result = alertPostgresDAO.create(createAlertModel, userId)
        Good(result)

      case BASE_PRICE =>
        createAlertModel.basePrice.map { basePrice =>
          val createdAlert = alertPostgresDAO.create(createAlertModel, userId)
          val createdBasePriceAlert = alertPostgresDAO.createBasePrice(createdAlert.id, basePrice)

          val result = createdAlert.copy(basePrice = Some(createdBasePriceAlert.basePrice))
          Good(result)
        }.getOrElse(Bad(BasePriceRequiredError).accumulating)

      case _ =>
        Bad(UnknownAlertTypeError).accumulating
    }
  }

  def markAsTriggered(alertId: AlertId): ApplicationResult[Unit] = withConnection { implicit conn =>
    val updatedRows = alertPostgresDAO.markAsTriggered(alertId)
    if (updatedRows == 1) {
      Good(())
    } else {
      Bad(AlertNotFound).accumulating
    }
  }
  
  def findPendingAlertsForPrice(market: Market, book: Book, currentPrice: BigDecimal): ApplicationResult[List[Alert]] = withConnection { implicit conn =>
    if (currentPrice <= 0) {
      Bad(InvalidPriceError).accumulating
    } else {
      val alertList = alertPostgresDAO.findPendingAlertsForPrice(market, book, currentPrice)
      Good(alertList)
    }
  }
}
