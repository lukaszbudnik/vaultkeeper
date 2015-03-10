package com.github.lukaszbudnik.vaultkeeper.v1.keys

import java.util.Date

import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.ApiKey

case class Key(keyName: String, content: String)

case class KeyMetaData(keyName: String, isActive: Boolean, lastUpdated: Date, credentials: Seq[ApiKey])

case class KeyGet(keyName: String)

case class KeyGetMetaData(keyName: String)

case class KeyUpdate(keyName: String, isActive: Option[Boolean], credentials: Option[Seq[ApiKey]])

case class KeyUpdated(response: String = "key updated")

case class KeyRemove(keyName: String)

case class KeyRemoved(response: String = "key removed")

case class KeyAdd(key: Key, credentials: Seq[ApiKey])

case class KeyAdded(response: String = "key added")
