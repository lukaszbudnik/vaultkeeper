package com.github.lukaszbudnik.vaultkeeper.v1.keys

class KeyStoreServiceImpl extends KeyStoreService {

  lazy val keys: Map[String, String] = Map(
    ("existing" -> "exists"),
    ("abc-public" -> "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsODCv95RSr8He9klKuHO\nNFfmmIQ/96xZ7xYgIxODSa0uaMOWRowyLLVYdp0yT4o+8w0CNxRHMWSNWSy+AIqF\nyrLHErqqzFZ5uoUWvsiXm+jquweFxYCC5JrnFoD6zix3fP/4NOjdIGTbrACOCFp6\n1fduC3wyMzbfNL8xGkZkvvAXvx0DXkzkknnK/1MN+1k03TkAXPXxSpV03I/Dk9mu\nMFNdlm1UiqPJo883x5kxX2+MDUgxv+SGxag9VIqZjOSp8b0J+mOQ5OmETN0kQE78\ndDcSfWciI1z+fLcePRL+yj1FJmufNtoIIOlJMWr6H1Kd2CwyliikSgy57WxS3VHv\nPQIDAQAB\n-----END PUBLIC KEY-----")
  )

  override def get(name: String): Option[String] = keys.get(name)

}
