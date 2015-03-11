package com.github.lukaszbudnik.vaultkeeper.v1.keys

import java.util.Date

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import com.github.lukaszbudnik.vaultkeeper.testkit.VaultKeeperTestKit
import com.github.lukaszbudnik.vaultkeeper.v1.auth.apikey.ApiKey
import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KeyServiceInMemorySpec extends Specification with Mockito {

  sequential

  val keyService = new KeyServiceInMemoryImpl

  val keyName = "keyName"
  val key = Key(keyName, "content")

  val apiKey1 = ApiKey("0123456789")
  val apiKey2 = ApiKey("9876543210")

  "KeyStoreActor" should {
    "add key" in {
      val keyAdd = KeyAdd(key, Seq(apiKey1))
      keyService.add(keyAdd)

      keyService.keys must haveSize(1)
    }

    "return key when found" in {
      val foundKey = keyService.get(keyName)

      foundKey must beSome(key)
    }

    "return None when not found" in {
      val foundKey = keyService.get(keyName + keyName)

      foundKey must beNone
    }

    "return key meta data when found" in {
      val keyMetaData = keyService.getMetaData(keyName)

      keyMetaData must beSome.which {
        _ match {
          case KeyMetaData(foundKeyName, isActive, lastUpdated, credentials) => {
            foundKeyName === keyName
            isActive === true
            lastUpdated must beAnInstanceOf[Date]
            credentials must contain(apiKey1)
          }
        }
      }
    }

    "update key isActive only" in {
      val keyUpdate = KeyUpdate(keyName, Some(false), None)
      keyService.update(keyUpdate)

      val updatedKey = keyService.get(keyName)
      updatedKey must beSome(key)

      val keyMetaData = keyService.getMetaData(keyName)
      keyMetaData must beSome.which {
        _ match {
          case KeyMetaData(foundKeyName, isActive, lastUpdated, credentials) => {
            foundKeyName === keyName
            isActive === false
            lastUpdated must beAnInstanceOf[Date]
            credentials must contain(apiKey1)
          }
        }
      }
    }

    "update key credentials only" in {
      val keyUpdate = KeyUpdate(keyName, None, Some(Seq(apiKey2)))
      keyService.update(keyUpdate)

      val updatedKey = keyService.get(keyName)
      updatedKey must beSome(key)

      val keyMetaData = keyService.getMetaData(keyName)
      keyMetaData must beSome.which {
        _ match {
          case KeyMetaData(foundKeyName, isActive, lastUpdated, credentials) => {
            foundKeyName === keyName
            isActive === false
            lastUpdated must beAnInstanceOf[Date]
            credentials must not contain(apiKey1)
            credentials must contain(apiKey2)
          }
        }
      }
    }

    "update key all in one" in {
      val keyUpdate = KeyUpdate(keyName, Some(true), Some(Seq(apiKey1, apiKey2)))
      keyService.update(keyUpdate)

      val updatedKey = keyService.get(keyName)
      updatedKey must beSome(key)

      val keyMetaData = keyService.getMetaData(keyName)
      keyMetaData must beSome.which {
        _ match {
          case KeyMetaData(foundKeyName, isActive, lastUpdated, credentials) => {
            foundKeyName === keyName
            isActive === true
            lastUpdated must beAnInstanceOf[Date]
            credentials must contain(apiKey1)
            credentials must contain(apiKey2)
          }
        }
      }
    }

    "remove key" in {
      keyService.remove(keyName)

      keyService.keys must beEmpty
    }

  }

}
