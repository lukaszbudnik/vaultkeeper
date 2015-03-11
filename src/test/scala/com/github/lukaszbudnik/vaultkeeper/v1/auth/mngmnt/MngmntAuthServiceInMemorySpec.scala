package com.github.lukaszbudnik.vaultkeeper.v1.auth.mngmnt

import org.junit.runner.RunWith
import org.mockito.Matchers.{anyString => isAnyString, eq => isEq}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MngmntAuthServiceInMemorySpec extends Specification with Mockito {

  sequential

  val mngmgntAuthService = new MngmntAuthServiceInMemoryImpl

  val username = "lukasz"
  val password = "budnik"


  "MngmntAuthServiceInMemoryImpl" should {

    "remove user" in {
      mngmgntAuthService.remove(username)

      mngmgntAuthService.users must beEmpty
    }

    "add user" in {
      mngmgntAuthService.add(username, password)

      mngmgntAuthService.users must haveSize(1)
    }

    "authenticate" in {
      val user = mngmgntAuthService.authenticate(username, password)

      user must beSome.which {
        _ match {
          case User(u) => username === u
        }
      }
    }

  }

}
