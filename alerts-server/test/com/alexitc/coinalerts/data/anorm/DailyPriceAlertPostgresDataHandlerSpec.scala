package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.commons.DataHelper.createUnverifiedUser
import com.alexitc.coinalerts.commons.{DataHelper, PostgresDataHandlerSpec, RandomDataGenerator}
import com.alexitc.coinalerts.core.{Count, Limit, Offset, PaginatedQuery}
import com.alexitc.coinalerts.data.anorm.dao.DailyPriceAlertPostgresDAO
import com.alexitc.coinalerts.errors.RepeatedDailyPriceAlertError
import com.alexitc.coinalerts.models.{Book, CreateDailyPriceAlertModel, Exchange, UserId}
import org.scalactic.Bad

class DailyPriceAlertPostgresDataHandlerSpec extends PostgresDataHandlerSpec {

  lazy val dailyPriceAlertDataHandler = new DailyPriceAlertPostgresDataHandler(database, new DailyPriceAlertPostgresDAO)

  "Creating a daily price alert" should {
    "be able to create a valid alert" in {
      val user = DataHelper.createVerifiedUser()
      val model = CreateDailyPriceAlertModel(Exchange.BITTREX, Book.fromString("BTC_ETH").get)
      val result = dailyPriceAlertDataHandler.create(user.id, model).get

      result.book mustEqual model.book
      result.market mustEqual model.market
      result.userId mustEqual user.id
    }

    "reject a repeated alert" in {
      val user = DataHelper.createVerifiedUser()
      val model = CreateDailyPriceAlertModel(Exchange.BITTREX, Book.fromString("BTC_ETH").get)
      dailyPriceAlertDataHandler.create(user.id, model)
      val result = dailyPriceAlertDataHandler.create(user.id, model)

      result mustEqual Bad(RepeatedDailyPriceAlertError).accumulating
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
      dailyPriceAlertDataHandler.create(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("ETH", "MXN")))

      val query = PaginatedQuery(Offset(1), Limit(1))
      val result = dailyPriceAlertDataHandler.getAlerts(user.id, query).get
      result.data.isEmpty mustEqual true
      result.total mustEqual Count(1)
    }

    "return a result that is paginated properly" in {
      val user = createUnverifiedUser()
      dailyPriceAlertDataHandler.create(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("ETH", "MXN")))
      dailyPriceAlertDataHandler.create(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("BTC", "MXN")))

      val query = PaginatedQuery(Offset(0), Limit(1))
      val result = dailyPriceAlertDataHandler.getAlerts(user.id, query).get
      result.offset mustEqual query.offset
      result.limit mustEqual query.limit
      result.total mustEqual Count(2)
      result.data.length mustEqual query.limit.int
    }

    "return a result for the second page different to the one on the first page" in {
      val user = createUnverifiedUser()
      dailyPriceAlertDataHandler.create(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("ETH", "MXN")))
      dailyPriceAlertDataHandler.create(user.id, RandomDataGenerator.createDailyPriceAlertModel(book = Book("BTC", "MXN")))

      val page1Query = PaginatedQuery(Offset(0), Limit(1))
      val page1Result = dailyPriceAlertDataHandler.getAlerts(user.id, page1Query).get

      val page2Query = PaginatedQuery(Offset(1), Limit(1))
      val page2Result = dailyPriceAlertDataHandler.getAlerts(user.id, page2Query).get

      page1Result.data.head.id mustNot be(page2Result.data.head.id)
    }
  }
}
