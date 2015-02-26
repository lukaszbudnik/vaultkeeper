package com.github.lukaszbudnik.vaultkeeper.v1.keys

trait KeyStoreService {

  def get(name: String): Option[String]

}
