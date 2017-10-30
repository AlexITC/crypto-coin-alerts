package com.alexitc.coinalerts.modules

import com.alexitc.coinalerts.data.UserDAL
import com.alexitc.coinalerts.data.anorm.UserPostgresDAL
import com.google.inject.AbstractModule

class UserDALModule extends AbstractModule {

  override def configure() = {
    bind(classOf[UserDAL]).to(classOf[UserPostgresDAL])
  }
}
