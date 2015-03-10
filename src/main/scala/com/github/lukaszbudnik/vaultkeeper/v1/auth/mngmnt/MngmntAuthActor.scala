package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

import javax.inject.Inject

import akka.actor.Actor


class MngmntAuthActor @Inject()(mngmntUserStoreService: MngmntUserAuthService) extends Actor {


  override def receive: Receive = _ match {

    case userAuth: UserAuth => {
      val user = mngmntUserStoreService.authenticate(userAuth.username, userAuth.password)
      sender ! user
    }

    case userAdd: UserAdd => {
      mngmntUserStoreService.add(userAdd.username, userAdd.password)
      sender ! UserAdded()
    }

    case userRemove: UserRemove => {
      mngmntUserStoreService.remove(userRemove.username)
      sender ! UserRemoved()
    }

  }
}
