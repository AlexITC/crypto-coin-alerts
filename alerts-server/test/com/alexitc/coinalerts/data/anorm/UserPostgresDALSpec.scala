package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.common.{PostgresDALSpec, RandomDataGenerator}
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One}

class UserPostgresDALSpec extends PostgresDALSpec {

  lazy val userPostgresDAL = new UserPostgresDAL(database)

  "Creating a user" should {
    "Allow to create a new user" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      val result = userPostgresDAL.create(email, password)

      result.isGood mustEqual true
      val user = result.get
      user.email mustEqual email
    }

    "Fail to create a new user when the email already exists" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      userPostgresDAL.create(email, password).isGood mustEqual true

      val result = userPostgresDAL.create(email.copy(string = email.string.toUpperCase), password)
      result mustEqual Bad(EmailAlreadyExists).accumulating
    }
  }

  "Creating a verification token" should {
    "Allow to create a verification token" in {
      val email = RandomDataGenerator.email
      val userId = userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword).get.id

      val tokenResult = userPostgresDAL.createVerificationToken(userId)
      tokenResult.isGood mustEqual true
    }

    "Fail to create a verification token for a user that already has one" in {
      val email = RandomDataGenerator.email
      val userId = userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword).get.id

      userPostgresDAL.createVerificationToken(userId).isGood mustEqual true
      userPostgresDAL.createVerificationToken(userId) mustEqual Bad(UserVerificationTokenAlreadyExists).accumulating
    }

    "Fail to create a verification token for an unknown user" in {
      val userId = new UserId("no-one")

      val tokenResult = userPostgresDAL.createVerificationToken(userId)
      tokenResult.isBad mustEqual true
      tokenResult.swap.get.head.isInstanceOf[PostgresIntegrityViolationError] mustEqual true
    }
  }

  "Verifying a user token" should {
    "Allow to verify user email by token" in {
      val userId = userPostgresDAL.create(RandomDataGenerator.email, RandomDataGenerator.hiddenPassword).get.id
      val token = userPostgresDAL.createVerificationToken(userId).get
      val result = userPostgresDAL.verifyEmail(token)
      result.isGood mustEqual true
      result.get.id mustEqual userId
    }

    "Fail to verify user email given an invalid token" in {
      val token = new UserVerificationToken("no-one")
      val result = userPostgresDAL.verifyEmail(token)
      result mustEqual Bad(UserVerificationTokenNotFound).accumulating
    }
  }

  "Retrieving user password" should {
    "Allow to retrieve the passsword" in {
      val email = RandomDataGenerator.email
      val password = RandomDataGenerator.hiddenPassword
      val _ = createVerifiedUser(email, password)

      val result = userPostgresDAL.getVerifiedUserPassword(email)
      result mustEqual Good(password)
    }

    "Fail to retrieve the password for an unknown user" in {
      val result = userPostgresDAL.getVerifiedUserPassword(RandomDataGenerator.email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve the password for an unverified user" in {
      val email = RandomDataGenerator.email
      userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword)

      val result = userPostgresDAL.getVerifiedUserPassword(email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  "Retrieving a user by email" should {
    "Allow to retrieve the user" in {
      val email = RandomDataGenerator.email
      val user = createVerifiedUser(email)
      val result = userPostgresDAL.getVerifiedUserByEmail(email)
      result mustEqual Good(User(user.id, email))
    }

    "Fail to retrieve an unknown user" in {
      val result = userPostgresDAL.getVerifiedUserByEmail(RandomDataGenerator.email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve an unverified user" in {
      val email = RandomDataGenerator.email
      userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword)

      val result = userPostgresDAL.getVerifiedUserByEmail(email)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  "Retrieving a user by id" should {
    "Allow to retrieve the user" in {
      val email = RandomDataGenerator.email
      val user = createVerifiedUser(email)
      val result = userPostgresDAL.getVerifiedUserById(user.id)
      result mustEqual Good(user)
    }

    "Fail to retrieve an unknown user" in {
      val result = userPostgresDAL.getVerifiedUserById(UserId.create)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }

    "Fail to retrieve an unverified user" in {
      val email = RandomDataGenerator.email
      val user = userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword).get

      val result = userPostgresDAL.getVerifiedUserById(user.id)
      result mustEqual Bad(One(VerifiedUserNotFound))
    }
  }

  def createVerifiedUser(email: UserEmail, password: UserHiddenPassword = RandomDataGenerator.hiddenPassword) = {
    val user = userPostgresDAL.create(email, password).get
    val token = userPostgresDAL.createVerificationToken(user.id).get

    userPostgresDAL.verifyEmail(token).get
  }
}
