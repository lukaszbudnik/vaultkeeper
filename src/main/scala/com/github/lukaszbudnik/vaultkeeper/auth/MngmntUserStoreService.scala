package com.github.lukaszbudnik.vaultkeeper.auth

case class User(username: String, apiKey: String)

trait MngmntUserStoreService {

  def authenticate(username: String, apiKey: String, context: String, signature: String): Option[User]

}
