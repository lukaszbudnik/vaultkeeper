package com.github.lukaszbudnik.vaultkeeper

import java.nio.charset.StandardCharsets
import java.security.Security

import org.bouncycastle.jce.provider.BouncyCastleProvider

package object utils {

  Security.addProvider(new BouncyCastleProvider)

  lazy val SECURITY_PROVIDER = BouncyCastleProvider.PROVIDER_NAME

  lazy val RSA_ALGORITHM = "RSA"
  lazy val RSA_FULL_ALGORITHM = "RSA/ECB/PKCS1Padding"
  lazy val RSA_KEY_LENGTH = 2048

}
