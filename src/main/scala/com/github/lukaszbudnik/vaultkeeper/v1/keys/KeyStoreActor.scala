package com.github.lukaszbudnik.vaultkeeper.v1.keys

import javax.inject.Inject

import akka.actor.Actor

class KeyStoreActor @Inject()(keyStoreService: KeyStoreService) extends Actor {

  def receive = _ match {

    case name: String => {
      sender ! keyStoreService.get(name)
    }

  }

}
