package com.alexitc.coinalerts.data

import com.alexitc.coinalerts.commons.ApplicationResult
import com.alexitc.coinalerts.models.{User, UserEmail, UserHiddenPassword}

trait UserDAL {

  def create(email: UserEmail, password: UserHiddenPassword): ApplicationResult[User]
}
