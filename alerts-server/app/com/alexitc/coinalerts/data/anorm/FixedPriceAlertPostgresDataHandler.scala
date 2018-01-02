package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.FixedPriceAlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.FixedPriceAlertPostgresDAO
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good}
import play.api.db.Database

class FixedPriceAlertPostgresDataHandler @Inject() (
    protected val database: Database,
    alertPostgresDAO: FixedPriceAlertPostgresDAO)
    extends FixedPriceAlertBlockingDataHandler
    with AnormPostgresDAL{

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): ApplicationResult[FixedPriceAlert] = {
    val result = withConnection { implicit conn =>
      val result = alertPostgresDAO.create(createAlertModel, userId)
      Good(result)
    }

    result.badMap { errors =>
      errors.map {
        case PostgresIntegrityViolationError(Some("currency_id"), _) => UnknownExchangeCurrencyIdError
        case PostgresIntegrityViolationError(Some("user_id"), _) => VerifiedUserNotFound
        case e => e
      }
    }
  }

  override def markAsTriggered(alertId: FixedPriceAlertId): ApplicationResult[Unit] = withConnection { implicit conn =>
    val updatedRows = alertPostgresDAO.markAsTriggered(alertId)
    if (updatedRows == 1) {
      Good(())
    } else {
      Bad(FixedPriceAlertNotFoundError).accumulating
    }
  }

  override def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): ApplicationResult[List[FixedPriceAlert]] = withConnection { implicit conn =>
    if (currentPrice <= 0) {
      Bad(InvalidPriceError).accumulating
    } else {
      val alertList = alertPostgresDAO.findPendingAlertsForPrice(currencyId, currentPrice)
      Good(alertList)
    }
  }

  override def getAlerts(userId: UserId, query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlert]] = withConnection { implicit conn =>
    val alerts = alertPostgresDAO.getAlerts(userId, query)
    val total = alertPostgresDAO.countAlerts(userId)
    val result = PaginatedResult(query.offset, query.limit, total, alerts)
    Good(result)
  }
}
