package com.github.lukaszbudnik.vaultkeeper.auth

import javax.inject.Singleton

@Singleton
class MngmntUserStoreServiceImpl extends MngmntUserStoreService {

  override def authenticate(username: String, password: String): Option[User] = {
    if (username == "lukasz" && password == "budnik") {
      Some(User(username + password))
    } else {
      None
    }
  }

}
