package com.github.lukaszbudnik.vaultkeeper.auth

import spray.routing.authentication.UserPass

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class User(username: String)

trait MngmntAuthenticator {

  val mngmntUserStoreService: MngmntUserStoreService

  def userPassAuthenticator(userPass: Option[UserPass]): Future[Option[User]] = Future {

    userPass match {
      case Some(userPass) => {
        mngmntUserStoreService.authenticate(userPass.user, userPass.pass)
      }
      case _ => None
    }

  }

}

