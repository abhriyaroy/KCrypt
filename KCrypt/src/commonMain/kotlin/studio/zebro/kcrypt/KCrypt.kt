package studio.zebro.kcrypt

interface KCrypt {
  fun getEncryptionKey(keySize: Int = 64): ByteArray?

  fun getEncryptionKeyToHexString(keySize: Int = 64): String

  fun saveEncryptionKey(key: ByteArray)

  fun saveEncryptionKey(key: String, isHexString: Boolean)

  fun byteArrayToHexString(byteArray: ByteArray): String

  fun hexStringToByteArray(hexString: String): ByteArray?

  fun saveString(key: String, value: String)

  fun saveBoolean(key: String, value: Boolean)

  fun saveInt(key: String, value: Int)

  fun saveLong(key: String, value: Long)

  fun saveFloat(key: String, value: Float)

  fun saveDouble(key: String, value: Double)

  fun getString(key: String): String?

  fun getBoolean(key: String): Boolean?

  fun getInt(key: String): Int?

  fun getLong(key: String): Long?

  fun getFloat(key: String): Float?

  fun getDouble(key: String): Double?
}

expect fun getKCrypt(): KCrypt