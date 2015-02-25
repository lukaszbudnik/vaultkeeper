package com.github.lukaszbudnik.vaultkeeper.auth


import com.google.inject.Injector
import spray.routing.authentication.UserPass

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MngmntAuthenticator {
//  injector: Injector =>

//  val injector: Injector

  val mngmntUserStoreService: MngmntUserStoreService // = null //injector.getInstance(classOf[MngmntUserStoreService])

//  def initMngmntAuthenticator(injector: Injector): Unit = {
//    mngmntUserStoreService = injector.getInstance(classOf[MngmntUserStoreService])
//  }

  def userPassAuthenticator(userPass: Option[UserPass]): Future[Option[String]] = Future {

    userPass match {
      case Some(userPass) => {
        mngmntUserStoreService.authenticate(userPass.user, userPass.pass)
      }
      case _ => None
    }

  }

}

