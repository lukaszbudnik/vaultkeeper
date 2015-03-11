package com.github.lukaszbudnik.vaultkeeper.v1.keys

import javax.inject.Inject

import akka.actor.Actor

class KeyActor @Inject()(keyService: KeyService) extends Actor {

  override def receive = _ match {

    case keyGet: KeyGet => {
      val key = keyService.get(keyGet.keyName)
      sender ! key
    }

    case keyGetMetaData: KeyGetMetaData => {
      val keyMetaData = keyService.getMetaData(keyGetMetaData.keyName)
      sender ! keyMetaData
    }

    case keyRemove: KeyRemove => {
      keyService.remove(keyRemove.keyName)
      sender ! KeyRemoved()
    }

    case keyAdd: KeyAdd => {
      keyService.add(keyAdd)
      sender ! KeyAdded()
    }

    case keyUpdate: KeyUpdate => {
      keyService.update(keyUpdate)
      sender ! KeyUpdated()
    }

  }

}
