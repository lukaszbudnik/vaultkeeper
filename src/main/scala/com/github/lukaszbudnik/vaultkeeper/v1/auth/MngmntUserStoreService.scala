package com.github.lukaszbudnik.vaultkeeper.v1.auth

trait MngmntUserStoreService {

  def authenticate(username: String, password: String): Option[User]

}
