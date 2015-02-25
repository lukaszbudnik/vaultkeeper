package com.github.lukaszbudnik.vaultkeeper.keys

import javax.inject.Inject

import akka.actor.Actor

class KeyStoreActor @Inject() (keyStoreService: KeyStoreService) extends Actor {

  def receive = _ match {

    case name: String => {
      sender ! keyStoreService.get(name)
    }

  }

}
