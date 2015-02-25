package com.github.lukaszbudnik.vaultkeeper.utils

import java.io.StringReader

import org.apache.commons.io.IOUtils
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PemUtilsSpec extends Specification {

  "PemUtils" should {

    "convert keys to/from pem" in {
      val privateKeyPemFromResource = IOUtils.toString(getClass.getResourceAsStream("/test-rsa-private.pem"))
      val publicKeyPemFromResource = IOUtils.toString(getClass.getResourceAsStream("/test-rsa-public.pem"))

      val privateKey = PemUtils.toPrivateKey(privateKeyPemFromResource)
      val publicKey = PemUtils.toPublicKey(publicKeyPemFromResource)

      privateKey must not beNull

      publicKey must not beNull

      val privateKeyPem = PemUtils.toPem(privateKey)
      val publicKeyPem = PemUtils.toPem(publicKey)


      privateKeyPem must beEqualTo(privateKeyPemFromResource)

      publicKeyPem must beEqualTo(publicKeyPemFromResource)
    }

  }

}
