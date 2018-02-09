package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.commons.{PostgresDataHandlerSpec, RandomDataGenerator}
import com.alexitc.coinalerts.data.anorm.dao.UserPostgresDAO
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One}
import play.api.i18n.Lang

class UserPostgresDataHandlerSpec extends PostgresDataHandlerSpec {

  lazy val userPostgresDataHandler = new UserPostgresDataHandler(database, new UserPostgresDAO)

  "Creating a user" should {
    "Allow to create a new user" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      val result = userPostgresDataHandler.create(email, password)

      result.isGood mustEqual true
      val user = result.get
      user.email mustEqual email
    }

    "Fail to create a new user when the email already exists" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      userPostgresDataHandler.create(email, password).isGood mustEqual true

      val result = userPostgresDataHandler.create(email.copy(string = email.string.toUpperCase), password)
      result mustEqual Bad(EmailAlreadyExistsError).accumulating
    }
  }

  "Creating a verification token" should {
    "Allow to create a verification token" in {
      val email = RandomDataGenerator.email
      val userId = userPostgresDataHandler.create(email, RandomDataGenerator.hiddenPassword).get.id

      val tokenResult = userPostgresDataHandler.createVerificationToken(userId)
      tokenResult.isGood mustEqual true
    }

    "Fail to create a verification token for a user that already has one" in {
      val email = RandomDataGenerator.email
      val userId = userPostgresDataHandler.create(email, RandomDataGenerator.hiddenPassword).get.id

      userPostgresDataHandler.createVerificationToken(userId).isGood mustEqual true
      userPostgresDataHandler.createVerificationToken(userId) mustEqual Bad(UserVerificationTokenAlreadyExistsError).accumulating
    }

    "Fail to create a verification token for an unknown user" in {
      val userId = new UserId("no-one")

      val tokenResult = userPostgresDataHandler.createVerificationToken(userId)
      tokenResult.isBad mustEqual true
      tokenResult.swap.get.head.isInstanceOf[PostgresIntegrityViolationError] mustEqual true
    }
  }

  "Verifying a user token" should {
    "Allow to verify user email by token" in {
      val userId = userPostgresDataHandler.create(RandomDataGenerator.email, RandomDataGenerator.hiddenPassword).get.id
      val token = userPostgresDataHandler.createVerificationToken(userId).get
      val result = userPostgresDataHandler.verifyEmail(token)
      result.isGood mustEqual true
      result.get.id mustEqual userId
    }

    "Fail to verify user email given an invalid token" in {
      val token = new UserVerificationToken("no-one")
      val result = userPostgresDataHandler.verifyEmail(token)
      result mustEqual Bad(UserVerificationTokenNotFoundError).accumulating
    }
  }

  "Retrieving user password" should {
    "Allow to retrieve the passsword" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      val _ = createVerifiedUser(email, password)

      val result = userPostgresDataHandler.getVerifiedUserPassword(email)
      result mustEqual Good(password)
    }

    "Fail to retrieve the password for an unknown user" in {
      val result = userPostgresDataHandler.getVerifiedUserPassword(RandomDataGenerator.email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve the password for an unverified user" in {
      val email = RandomDataGenerator.email
      userPostgresDataHandler.create(email, RandomDataGenerator.hiddenPassword)

      val result = userPostgresDataHandler.getVerifiedUserPassword(email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  "Retrieving a user by email" should {
    "Allow to retrieve the user" in {
      val email = RandomDataGenerator.email
      val user = createVerifiedUser(email)
      val result = userPostgresDataHandler.getVerifiedUserByEmail(email)
      result mustEqual Good(User(user.id, email))
    }

    "Fail to retrieve an unknown user" in {
      val result = userPostgresDataHandler.getVerifiedUserByEmail(RandomDataGenerator.email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve an unverified user" in {
      val email = RandomDataGenerator.email
      userPostgresDataHandler.create(email, RandomDataGenerator.hiddenPassword)

      val result = userPostgresDataHandler.getVerifiedUserByEmail(email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  "Retrieving a user by id" should {
    "Allow to retrieve the user" in {
      val email = RandomDataGenerator.email
      val user = createVerifiedUser(email)
      val result = userPostgresDataHandler.getVerifiedUserById(user.id)
      result mustEqual Good(user)
    }

    "Fail to retrieve an unknown user" in {
      val result = userPostgresDataHandler.getVerifiedUserById(UserId.create)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve an unverified user" in {
      val email = RandomDataGenerator.email
      val user = userPostgresDataHandler.create(email, RandomDataGenerator.hiddenPassword).get

      val result = userPostgresDataHandler.getVerifiedUserById(user.id)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  "Retrieving user preferences" should {
    "Succeed when the user exists" in {
      val user = createVerifiedUser(RandomDataGenerator.email)
      val preferences = userPostgresDataHandler.getUserPreferences(user.id)
      preferences.isGood mustEqual true
    }

    "Succeed even if the user doesn't exists" in {
      val preferences = userPostgresDataHandler.getUserPreferences(UserId.create)
      preferences.isGood mustEqual true
    }
  }

  "Setting user preferences" should {
    "update the preferences" in {
      val user = createVerifiedUser(RandomDataGenerator.email)
      val lang = Lang("es")
      val preferencesModel = SetUserPreferencesModel.default.copy(lang = lang)

      val result = userPostgresDataHandler.setUserPreferences(user.id, preferencesModel).get
      result.lang mustEqual lang
    }

    "fail when the user doesn't exist" in {
      val preferencesModel = SetUserPreferencesModel.default

      val result = userPostgresDataHandler.setUserPreferences(UserId.create, preferencesModel)
      result mustEqual Bad(VerifiedUserNotFound).accumulating
    }
  }

  def createVerifiedUser(email: UserEmail, password: UserHiddenPassword = RandomDataGenerator.hiddenPassword) = {
    val user = userPostgresDataHandler.create(email, password).get
    val token = userPostgresDataHandler.createVerificationToken(user.id).get

    userPostgresDataHandler.verifyEmail(token).get
  }
}
