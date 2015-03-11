package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

import javax.inject.Singleton

@Singleton
class MngmntAuthServiceInMemoryImpl extends MngmntAuthService {

  private[mngmnt] var users = Map("lukasz" -> "budnik")

  override def authenticate(username: String, password: String): Option[User] = {
    users.find(i => i._1 == username && i._2 == password).map(i => User(i._1))
  }

  override def add(username: String, password: String) = {
    users = users + (username -> password)
  }

  override def remove(username: String) = {
    users = users.filterNot(_._1 == username)
  }

}
