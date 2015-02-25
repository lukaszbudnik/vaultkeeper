package com.github.lukaszbudnik.vaultkeeper.auth

import javax.inject.Singleton

@Singleton
class MngmntUserStoreServiceImpl extends MngmntUserStoreService {

  override def authenticate(username: String, password: String, context: String, signature: String): Option[String] = {
    if (username == "lukasz" && password == "budnik") {
      Some(username + password)
    } else {
      None
    }
  }

}
