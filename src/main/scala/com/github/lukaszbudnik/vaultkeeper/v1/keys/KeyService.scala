package com.github.lukaszbudnik.vaultkeeper.v1.keys

trait KeyService {

  def add(keyAdd: KeyAdd)

  def getMetaData(keyName: String): Option[KeyMetaData]

  def get(keyName: String): Option[Key]

  def update(keyUpdate: KeyUpdate): Option[KeyMetaData]

  def remove(keyName: String)

}
