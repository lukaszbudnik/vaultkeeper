package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

trait MngmntAuthService {

  def authenticate(username: String, password: String): Option[User]

  def add(username: String, password: String)

  def remove(username: String)

}
