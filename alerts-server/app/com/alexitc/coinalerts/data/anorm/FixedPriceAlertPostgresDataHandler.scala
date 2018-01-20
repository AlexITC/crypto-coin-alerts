package com.alexitc.coinalerts.data.anorm

import javax.inject.Inject

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.core.{Count, PaginatedQuery, PaginatedResult}
import com.alexitc.coinalerts.data.FixedPriceAlertBlockingDataHandler
import com.alexitc.coinalerts.data.anorm.dao.{ExchangeCurrencyPostgresDAO, FixedPriceAlertPostgresDAO}
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One, Or}
import play.api.db.Database

class FixedPriceAlertPostgresDataHandler @Inject() (
    protected val database: Database,
    exchangeCurrencyPostgresDAO: ExchangeCurrencyPostgresDAO,
    alertPostgresDAO: FixedPriceAlertPostgresDAO)
    extends FixedPriceAlertBlockingDataHandler
    with AnormPostgresDAL{

  override def create(createAlertModel: CreateFixedPriceAlertModel, userId: UserId): ApplicationResult[FixedPriceAlertWithCurrency] = {
    val result = withConnection { implicit conn =>
      val fixedPriceAlert = alertPostgresDAO.create(createAlertModel, userId)

      // the alert has a FK to the currency, hence it must exist
      val exchangeCurrency = exchangeCurrencyPostgresDAO.getBy(createAlertModel.exchangeCurrencyId).get
      val alert = FixedPriceAlertWithCurrency.from(fixedPriceAlert, exchangeCurrency)

      Good(alert)
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

  override def findPendingAlertsForPrice(currencyId: ExchangeCurrencyId, currentPrice: BigDecimal): ApplicationResult[List[FixedPriceAlertWithCurrency]] = withConnection { implicit conn =>
    if (currentPrice <= 0) {
      Bad(InvalidPriceError).accumulating
    } else {
      val alertList = alertPostgresDAO.findPendingAlertsForPrice(currencyId, currentPrice)
      Good(alertList)
    }
  }

  override def getAlerts(
      filterConditions: FixedPriceAlertFilter.Conditions,
      orderByConditions: FixedPriceAlertOrderBy.Conditions,
      query: PaginatedQuery): ApplicationResult[PaginatedResult[FixedPriceAlertWithCurrency]] = withConnection { implicit conn =>

    val alerts = alertPostgresDAO.getAlerts(filterConditions, orderByConditions, query)
    val total = alertPostgresDAO.countBy(filterConditions)
    val result = PaginatedResult(query.offset, query.limit, total, alerts)

    Good(result)
  }

  override def countBy(conditions: FixedPriceAlertFilter.Conditions): ApplicationResult[Count] = withConnection { implicit conn =>
    val result = alertPostgresDAO.countBy(conditions)
    Good(result)
  }

  override def delete(id: FixedPriceAlertId, userId: UserId): ApplicationResult[FixedPriceAlertWithCurrency] = withConnection { implicit conn =>
    val deletedAlertMaybe = alertPostgresDAO.delete(id, userId).map { alert =>
      // the alert has a FK to the currency, hence it must exist
      val exchangeCurrency = exchangeCurrencyPostgresDAO.getBy(alert.exchangeCurrencyId).get
      FixedPriceAlertWithCurrency.from(alert, exchangeCurrency)
    }

    Or.from(deletedAlertMaybe, One(FixedPriceAlertNotFoundError))
  }
}
