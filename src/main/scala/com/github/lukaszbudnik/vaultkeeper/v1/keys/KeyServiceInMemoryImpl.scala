package com.github.lukaszbudnik.vaultkeeper.v1.keys

import java.util.Date
import javax.inject.Singleton

import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.ApiKey

@Singleton
class KeyServiceInMemoryImpl extends KeyService {

  private case class InternalKey(keyName: String, isActive: Boolean, lastUpdated: Date, content: String, credentials: Seq[ApiKey]) {
    def toKey: Key = {
      Key(keyName, content)
    }
    def toKeyMetaData: KeyMetaData = {
      KeyMetaData(keyName, isActive, lastUpdated, credentials)
    }
  }

  private var keys: Map[String, InternalKey] = Map()

  override def get(keyName: String): Option[Key] = keys.get(keyName).map(_.toKey)

  override def getMetaData(keyName: String): Option[KeyMetaData] = keys.get(keyName).map(_.toKeyMetaData)

  override def insert(keyAdd: KeyAdd): Unit = {
    keys = keys + (keyAdd.key.keyName -> InternalKey(keyAdd.key.keyName, true, new Date, keyAdd.key.content, keyAdd.credentials))
  }

  override def update(keyUpdate: KeyUpdate): Unit = {
    val key = keys.get(keyUpdate.keyName)
    key match {
      case Some(key) => {

        val keyWithCredentials = keyUpdate.credentials match {
          case Some(credentials) => key.copy(credentials = credentials)
          case None => key
        }

        val keyWithCredentialsAndIsActive = keyUpdate.isActive match {
          case Some(isActive) => keyWithCredentials.copy(isActive = isActive)
          case None => keyWithCredentials
        }

        val keyToUpdate = keyWithCredentialsAndIsActive.copy(lastUpdated = new Date)

        keys = keys + (keyToUpdate.keyName -> keyToUpdate)

      }
      case None =>
    }
  }

  override def remove(keyName: String): Unit = {
    keys = keys.filterNot(_._1 == keyName)
  }
}
