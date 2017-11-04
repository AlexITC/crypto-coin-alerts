package com.alexitc.coinalerts.data.anorm

import com.alexitc.coinalerts.common.{PostgresDALSpec, RandomDataGenerator}
import com.alexitc.coinalerts.errors._
import com.alexitc.coinalerts.models._
import org.scalactic.{Bad, Good, One}

class UserPostgresDALSpec extends PostgresDALSpec {

  lazy val userPostgresDAL = new UserPostgresDAL(database)

  /** Create user **/
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

  /** Create token **/
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

  /** Verify user **/
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

  /** Get password **/
  it should "Allow to retrieve the passsword" in {
    val email = RandomDataGenerator.email
    val password = RandomDataGenerator.hiddenPassword
    val _ = createVerifiedUser(email, password)

    val result = userPostgresDAL.getVerifiedUserPassword(email)
    result shouldBe Good(password)
  }

  it should "Fail to retrieve the password for an unknown user" in {
    val result = userPostgresDAL.getVerifiedUserPassword(RandomDataGenerator.email)
    result shouldBe Bad(One(VerifiedUserNotFound))
  }

  it should "Fail to retrieve the password for an unverified user" in {
    val email = RandomDataGenerator.email
    userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword)

    val result = userPostgresDAL.getVerifiedUserPassword(email)
    result shouldBe Bad(One(VerifiedUserNotFound))
  }

  /** Get user by email **/
  it should "Allow to retrieve the user" in {
    val email = RandomDataGenerator.email
    val user = createVerifiedUser(email)
    val result = userPostgresDAL.getVerifiedUserByEmail(email)
    result shouldBe Good(User(user.id, email))
  }

  it should "Fail to retrieve an unknown user" in {
    val result = userPostgresDAL.getVerifiedUserByEmail(RandomDataGenerator.email)
    result shouldBe Bad(One(VerifiedUserNotFound))
  }

  it should "Fail to retrieve an unverified user" in {
    val email = RandomDataGenerator.email
    userPostgresDAL.create(email, RandomDataGenerator.hiddenPassword)

    val result = userPostgresDAL.getVerifiedUserByEmail(email)
    result shouldBe Bad(One(VerifiedUserNotFound))
  }

  /** Helpers **/
  def createVerifiedUser(email: UserEmail, password: UserHiddenPassword = RandomDataGenerator.hiddenPassword) = {
    val user = userPostgresDAL.create(email, password).get
    val token = userPostgresDAL.createVerificationToken(user.id).get

    userPostgresDAL.verifyEmail(token).get
  }
}
