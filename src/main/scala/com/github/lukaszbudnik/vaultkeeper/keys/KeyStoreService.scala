package com.github.lukaszbudnik.vaultkeeper.keys

trait KeyStoreService {

  def get(name: String): Option[String]

}
