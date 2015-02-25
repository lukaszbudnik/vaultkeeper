package com.github.lukaszbudnik.vaultkeeper.auth

trait MngmntUserStoreService {

  def authenticate(username: String, password: String): Option[User]

}
