package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.commons.DataHelper.createUnverifiedUser
import com.alexitc.coinalerts.commons.{DataHelper, PostgresDataHandlerSpec, RandomDataGenerator}
import com.alexitc.coinalerts.data.anorm.dao.DailyPriceAlertPostgresDAO
import com.alexitc.coinalerts.errors.{RepeatedDailyPriceAlertError, UnknownExchangeCurrencyIdError}
import com.alexitc.coinalerts.models.{CreateDailyPriceAlertModel, ExchangeCurrencyId, UserId}
import com.alexitc.playsonify.models.{Count, Limit, Offset, PaginatedQuery}
import org.scalactic.Bad

class DailyPriceAlertPostgresDataHandlerSpec extends PostgresDataHandlerSpec {

  lazy val dailyPriceAlertDataHandler = new DailyPriceAlertPostgresDataHandler(database, new DailyPriceAlertPostgresDAO)

  "Creating a daily price alert" should {
    "be able to create a valid alert" in {
      val user = DataHelper.createVerifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val model = CreateDailyPriceAlertModel(RandomDataGenerator.item(currencies).id)
      val result = dailyPriceAlertDataHandler.create(user.id, model).get

      result.userId mustEqual user.id
    }

    "reject a repeated alert" in {
      val user = DataHelper.createVerifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val model = CreateDailyPriceAlertModel(RandomDataGenerator.item(currencies).id)
      dailyPriceAlertDataHandler.create(user.id, model)
      val result = dailyPriceAlertDataHandler.create(user.id, model)

      result mustEqual Bad(RepeatedDailyPriceAlertError).accumulating
    }

    "reject an alert with unknown exchangeCurrencyId" in {
      val user = DataHelper.createVerifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val exchangeCurrencyId = ExchangeCurrencyId(currencies.map(_.id.int).max + 1)
      val result = dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(exchangeCurrencyId))

      result mustEqual Bad(UnknownExchangeCurrencyIdError).accumulating
    }
  }

  "retrieving user alerts" should {
    "return empty result for non-existent user" in {
      val userId = UserId.create
      val query = PaginatedQuery(Offset(0), Limit(10))
      val result = dailyPriceAlertDataHandler.getAlerts(userId, query).get
      result.data.isEmpty mustEqual true
      result.total mustEqual Count(0)
    }

    "return empty result when the offset is greater than the total elements" in {
      val user = createUnverifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(RandomDataGenerator.item(currencies).id))

      val query = PaginatedQuery(Offset(1), Limit(1))
      val result = dailyPriceAlertDataHandler.getAlerts(user.id, query).get
      result.data.isEmpty mustEqual true
      result.total mustEqual Count(1)
    }

    "return a result that is paginated properly" in {
      val user = createUnverifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val currencyIdList = RandomDataGenerator.uniqueItems(currencies, 2).map(_.id)
      currencyIdList.foreach { currencyId =>
        dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(currencyId))
      }

      val query = PaginatedQuery(Offset(0), Limit(1))
      val result = dailyPriceAlertDataHandler.getAlerts(user.id, query).get
      result.offset mustEqual query.offset
      result.limit mustEqual query.limit
      result.total mustEqual Count(2)
      result.data.length mustEqual query.limit.int
    }

    "return a result for the second page different to the one on the first page" in {
      val user = createUnverifiedUser()
      val currencies = exchangeCurrencyDataHandler.getAll().get
      val currencyIdList = RandomDataGenerator.uniqueItems(currencies, 2).map(_.id)
      dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(currencyIdList.head))
      dailyPriceAlertDataHandler.create(user.id, CreateDailyPriceAlertModel(currencyIdList(1)))

      val page1Query = PaginatedQuery(Offset(0), Limit(1))
      val page1Result = dailyPriceAlertDataHandler.getAlerts(user.id, page1Query).get

      val page2Query = PaginatedQuery(Offset(1), Limit(1))
      val page2Result = dailyPriceAlertDataHandler.getAlerts(user.id, page2Query).get

      page1Result.data.head.id mustNot be(page2Result.data.head.id)
    }
  }
}
