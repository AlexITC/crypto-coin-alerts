package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.errors.EmailAlreadyExists
import com.alexitc.coinalerts.models.{User, UserEmail, UserHiddenPassword, UserId}
import org.scalactic.{Bad, Good}

import scala.collection.mutable

trait UserInMemoryDAL extends UserDAL {

  val list = mutable.ListBuffer[User]()

  override def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User] = {
    if (list.exists(_.email.string.equalsIgnoreCase(email.string))) {
      Bad(EmailAlreadyExists).accumulating
    } else {
      val newUser = User(UserId.create, email)
      list += newUser
      Good(newUser)
    }
  }
}
