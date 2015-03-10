package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

case class UserAuth(username: String, password: String)

case class User(username: String)

case class UserAdd(username: String, password: String)

case class UserAdded()

case class UserRemove(username: String)

case class UserRemoved()
