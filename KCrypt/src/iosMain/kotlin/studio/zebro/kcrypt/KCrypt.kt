package studio.zebro.kcrypt

import kotlinx.cinterop.memScoped
import platform.Foundation.NSString
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.*
import kotlinx.cinterop.*
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.darwin.OSStatus
import platform.darwin.noErr
import platform.Foundation.NSUUID
import platform.posix.arc4random_buf
import studio.zebro.kcrypt.entity.KCryptKeychainEntity

@OptIn(ExperimentalForeignApi::class)
class KCryptIos(
  private val iosPlatformHelper: IosPlatformHelper,
  private val keystoreManager: KeystoreManager
) : KCrypt {

  private val keyName: String = "KCryptKey"

  override fun getEncryptionKey(keySize: Int): ByteArray? {
    val preStoredEncryptionKey = keystoreManager.value(forKey = keyName)
    return if (preStoredEncryptionKey != null) {
      hexStringToByteArray(preStoredEncryptionKey.let {
        deSerialize(it)
          .let {
            if (it.isStringInHex) {
              it.key
            } else {
              stringToHex(it.key)
            }
          }
      })
    } else {
      val newEncryptionKey = iosPlatformHelper.generateRandomByteArray(keySize)
      addOrUpdate(
        keyName, KCryptKeychainEntity(byteArrayToHexString(newEncryptionKey), true)
      )
      newEncryptionKey
    }
  }

  override fun getEncryptionKeyToHexString(keySize: Int): String {
    return getEncryptionKey(keySize)?.let {
      byteArrayToHexString(it)
    } ?: "NA"
  }

  override fun saveEncryptionKey(key: String, isHexString: Boolean) {
    addOrUpdate(
      keyName, KCryptKeychainEntity(key, isHexString)
    )
  }

  override fun saveEncryptionKey(key: ByteArray) {
    addOrUpdate(keyName, KCryptKeychainEntity(byteArrayToHexString(key), true))
  }

  override fun byteArrayToHexString(byteArray: ByteArray): String {
    val hexChars = "0123456789ABCDEF".toCharArray()
    val result = StringBuilder()

    for (byte in byteArray) {
      val highNibble = (byte.toInt() ushr 4) and 0x0F
      val lowNibble = byte.toInt() and 0x0F
      result.append(hexChars[highNibble])
      result.append(hexChars[lowNibble])
    }
    return result.toString()
  }

  override fun hexStringToByteArray(hexString: String): ByteArray? {
    val result = ByteArray(hexString.length / 2)
    for (i in hexString.indices step 2) {
      val byte = hexString.substring(i, i + 2).toInt(16).toByte()
      result[i / 2] = byte
    }
    return result
  }

  override fun saveString(key: String, value: String) {
    addOrUpdate(key, value)
  }

  override fun getString(key: String): String? {
    return keystoreManager.value(key)
  }

  override fun saveBoolean(key: String, value: Boolean) {
    addOrUpdate(key, value.toString())
  }

  override fun getBoolean(key: String): Boolean? {
    return keystoreManager.value(key)?.let {
      it.equals("true", true)
    }
  }

  override fun saveDouble(key: String, value: Double) {
    addOrUpdate(key, value.toString())
  }

  override fun getDouble(key: String): Double? {
    return keystoreManager.value(key)?.toDouble()
  }

  override fun saveFloat(key: String, value: Float) {
    addOrUpdate(key, value.toString())
  }

  override fun getFloat(key: String): Float? {
    return keystoreManager.value(key)?.toFloat()
  }

  override fun saveInt(key: String, value: Int) {
    addOrUpdate(key, value.toString())
  }

  override fun getInt(key: String): Int? {
    return keystoreManager.value(key)?.toInt()
  }

  override fun saveLong(key: String, value: Long) {
    addOrUpdate(key, value.toString())
  }

  override fun getLong(key: String): Long? {
    return keystoreManager.value(key)?.toLong()
  }

  private fun addOrUpdate(key: String, value: KCryptKeychainEntity): Boolean {
    return if (keystoreManager.existsObject(key)) {
      keystoreManager.update(key, value)
    } else {
      println("in add key $key and value $value")
      keystoreManager.add(key, value)
    }
  }

  private fun addOrUpdate(key: String, value: String): Boolean {
    return if (keystoreManager.existsObject(key)) {
      keystoreManager.update(key, value)
    } else {
      keystoreManager.add(key, value)
    }
  }

  private fun stringToHex(input: String): String {
    memScoped {
      val data = input.cstr.ptr
      val length = input.length

      val hexString = StringBuilder()

      for (i in 0 until length) {
        val byteValue = data[i].toInt() and 0xFF // Convert UByte to Int
        hexString.append(
          byteValue.toString(16).padStart(2, '0')
        ) // Convert to hexadecimal and pad with zeros
      }

      return hexString.toString()
    }
  }

  private fun deSerialize(data: String): KCryptKeychainEntity {
    val json = Json { isLenient = true; ignoreUnknownKeys = true }
    return json.decodeFromString(KCryptKeychainEntity.serializer(), data)
  }

}

var kCryptInstance: KCrypt? = null

actual fun getKCrypt(): KCrypt =
  kCryptInstance ?: KCryptIos(IosPlatformHelperImpl(), KeystoreManagerImpl()).apply {
    kCryptInstance = this
  }