package com.github.lukaszbudnik.vaultkeeper.testkit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.After

class VaultKeeperTestKit(val _system: ActorSystem) extends TestKit(_system) with After with ImplicitSender {
  def after = system.shutdown()
}
