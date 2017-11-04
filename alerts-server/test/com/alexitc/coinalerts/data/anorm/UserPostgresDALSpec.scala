package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.common.{PostgresDALSpec, RandomDataGenerator}
import com.alexitc.coinalerts.errors.{EmailAlreadyExists, PostgresIntegrityViolationError, UserVerificationTokenAlreadyExists, UserVerificationTokenNotFound}
import com.alexitc.coinalerts.models.{UserId, UserVerificationToken}
import org.scalactic.Bad

class UserPostgresDALSpec extends PostgresDALSpec {

  lazy val userPostgresDAL = new UserPostgresDAL(database)

  it should "Allow to create a new user" in {
    val email = RandomDataGenerator.email
    val password = RandomDataGenerator.hiddenPassword
    val result = userPostgresDAL.create(email, password)

    result.isGood shouldBe true
    val user = result.get
    user.email shouldBe email
  }

  it should "Fail to create a new user when the email already exists" in {
    val email = RandomDataGenerator.email
    val password = RandomDataGenerator.hiddenPassword
    userPostgresDAL.create(email, password).isGood shouldBe true

    val result = userPostgresDAL.create(email.copy(string = email.string.toUpperCase), password)
    result shouldBe Bad(EmailAlreadyExists).accumulating
  }

  it should "Allow to create a verification token" in {
    val email = RandomDataGenerator.email
    val userId = userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword).get.id

    val tokenResult = userPostgresDAL.createVerificationToken(userId)
    tokenResult.isGood shouldBe true
  }

  it should "Fail to create a verification token for a user that already has one" in {
    val email = RandomDataGenerator.email
    val userId = userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword).get.id

    userPostgresDAL.createVerificationToken(userId).isGood shouldBe true
    userPostgresDAL.createVerificationToken(userId) shouldBe Bad(UserVerificationTokenAlreadyExists).accumulating
  }

  it should "Fail to create a verification token for an unknown user" in {
    val userId = new UserId("no-one")

    val tokenResult = userPostgresDAL.createVerificationToken(userId)
    tokenResult.isBad shouldBe true
    tokenResult.swap.get.head.isInstanceOf[PostgresIntegrityViolationError] shouldBe true
  }

  it should "Allow to verify user email by token" in {
    val userId = userPostgresDAL.create(RandomDataGenerator.email, RandomDataGenerator.hiddenPassword).get.id
    val token = userPostgresDAL.createVerificationToken(userId).get
    val result = userPostgresDAL.verifyEmail(token)
    result.isGood shouldBe true
    result.get.id shouldBe userId
  }

  it should "Fail to verify user email given an invalid token" in {
    val token = new UserVerificationToken("no-one")
    val result = userPostgresDAL.verifyEmail(token)
    result shouldBe Bad(UserVerificationTokenNotFound).accumulating
  }
}
