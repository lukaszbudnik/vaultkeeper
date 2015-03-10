package com.github.lukaszbudnik.vaultkeeper.v1.keys

trait KeyService {

  def insert(keyAdd: KeyAdd)

  def getMetaData(keyName: String): Option[KeyMetaData]

  def get(keyName: String): Option[Key]

  def update(keyUpdate: KeyUpdate)

  def remove(keyName: String)

}
