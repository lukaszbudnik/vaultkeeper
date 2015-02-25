package com.github.lukaszbudnik.vaultkeeper.utils

import java.io._
import java.nio.charset.StandardCharsets
import java.security._
import java.security.spec.{X509EncodedKeySpec, PKCS8EncodedKeySpec, KeySpec}

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.{PEMKeyPair, PEMParser}
import org.bouncycastle.openssl.jcajce.JcaPEMWriter

object PemUtils {

  def toPem(privateKey: PrivateKey): String = {
    val baos = new ByteArrayOutputStream
    val privateKeyPemWriter = new JcaPEMWriter(new PrintWriter(baos))
    privateKeyPemWriter.writeObject(privateKey)
    privateKeyPemWriter.close
    baos.toString(StandardCharsets.UTF_8.name())
  }

  def toPem(publicKey: PublicKey): String = {
    val baos = new ByteArrayOutputStream
    val privateKeyPemWriter = new JcaPEMWriter(new PrintWriter(baos))
    privateKeyPemWriter.writeObject(publicKey)
    privateKeyPemWriter.close
    baos.toString(StandardCharsets.UTF_8.name())
  }

  def toPrivateKey(pem: String): PrivateKey = {
    val reader = new StringReader(pem)
    val parser = new PEMParser(reader)
    val keyPair = parser.readObject.asInstanceOf[PEMKeyPair]
    val keyInfo = keyPair.getPrivateKeyInfo
    val encoded = keyInfo.getEncoded
    val kf: KeyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
    val keySpec = new PKCS8EncodedKeySpec(encoded)
    val key = kf.generatePrivate(keySpec)
    key
  }

  def toPublicKey(pem: String): PublicKey = {
    val reader = new StringReader(pem)
    val parser = new PEMParser(reader)
    val keyInfo = parser.readObject.asInstanceOf[SubjectPublicKeyInfo]
    val encoded = keyInfo.getEncoded
    val kf = KeyFactory.getInstance(RSA_ALGORITHM)
    val keySpec = new X509EncodedKeySpec(encoded)
    val key = kf.generatePublic(keySpec)
    key
  }

}
