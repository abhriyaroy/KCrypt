package studio.zebro.kcrypt

interface KCrypt {
  fun getEncryptionKey(keySize : Int = 64): ByteArray?

  fun getEncryptionKeyToHexString(keySize : Int = 64): String

  fun saveEncryptionKey(key: ByteArray)

  fun saveEncryptionKey(key: String, isHexString: Boolean)

  fun byteArrayToHexString(byteArray: ByteArray): String

  fun hexStringToByteArray(hexString: String): ByteArray?
}

expect fun getKCrypt(): KCrypt