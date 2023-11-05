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
class KCryptIos : KCrypt {

  val serviceName: String = "studio.zebro.kcrypt"
  val keyName: String = "KCryptKey"

  val accessibility: Accessible = Accessible.WhenUnlocked

  enum class Accessible(val value: CFStringRef?) {
    WhenPasscodeSetThisDeviceOnly(kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly), WhenUnlockedThisDeviceOnly(
      kSecAttrAccessibleWhenUnlockedThisDeviceOnly
    ),
    WhenUnlocked(kSecAttrAccessibleWhenUnlocked), AfterFirstUnlock(
      kSecAttrAccessibleAfterFirstUnlock
    ),
    AfterFirstUnlockThisDeviceOnly(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
  }

  override fun getEncryptionKey(keySize: Int): ByteArray? {
    val preStoredEncryptionKey = value(forKey = keyName)
    return if (preStoredEncryptionKey != null) {
      hexStringToByteArray(preStoredEncryptionKey.stringValue.let {
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
      val newEncryptionKey = generateRandomByteArray(keySize)
      addOrUpdate(
        keyName, KCryptKeychainEntity(byteArrayToHexString(newEncryptionKey), false)
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
    return value(key)?.stringValue
  }

  override fun saveBoolean(key: String, value: Boolean) {
    addOrUpdate(key, value.toString())
  }

  override fun getBoolean(key: String): Boolean? {
    return value(key)?.let {
      it.stringValue.equals("true", true)
    }
  }

  override fun saveDouble(key: String, value: Double) {
    addOrUpdate(key, value.toString())
  }

  override fun getDouble(key: String): Double? {
    return value(key)?.stringValue?.toDouble()
  }

  override fun saveFloat(key: String, value: Float) {
    addOrUpdate(key, value.toString())
  }

  override fun getFloat(key: String): Float? {
    return value(key)?.stringValue?.toFloat()
  }

  override fun saveInt(key: String, value: Int) {
    addOrUpdate(key, value.toString())
  }

  override fun getInt(key: String): Int? {
    return value(key)?.stringValue?.toInt()
  }

  private fun addOrUpdate(key: String, value: KCryptKeychainEntity): Boolean {
    return if (existsObject(key)) {
      update(key, value.toNsData())
    } else {
      add(key, value.toNsData())
    }
  }

  private fun addOrUpdate(key: String, value: String): Boolean {
    return if (existsObject(key)) {
      update(key, value.toNSData())
    } else {
      add(key, value.toNSData())
    }
  }

  private fun add(key: String, value: NSData?): Boolean = context(key, value) { (account, data) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecValueData to data,
      kSecAttrAccessible to accessibility.value
    )
    SecItemAdd(query, null).validate()
  }

  private fun update(key: String, value: Any?): Boolean = context(key, value) { (account, data) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecReturnData to kCFBooleanFalse,
    )

    val updateQuery = query(
      kSecValueData to data
    )
    SecItemUpdate(query, updateQuery).validate()
  }

  private fun value(forKey: String): NSData? = context(forKey) { (account) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecReturnData to kCFBooleanTrue,
      kSecMatchLimit to kSecMatchLimitOne,
    )

    memScoped {
      val result = alloc<CFTypeRefVar>()
      SecItemCopyMatching(query, result.ptr)
      CFBridgingRelease(result.value) as? NSData
    }
  }

  private fun existsObject(forKey: String): Boolean = context(forKey) { (account) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecReturnData to kCFBooleanFalse,
    )

    SecItemCopyMatching(query, null).validate()
  }


  private class Context(val refs: Map<CFStringRef?, CFTypeRef?>) {
    fun query(vararg pairs: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
      val map = mapOf(*pairs).plus(refs.filter { it.value != null })
      return CFDictionaryCreateMutable(
        null, map.size.convert(), null, null
      ).apply {
        map.entries.forEach { CFDictionaryAddValue(this, it.key, it.value) }
      }.apply {
        CFAutorelease(this)
      }
    }
  }

  private fun <T> context(vararg values: Any?, block: Context.(List<CFTypeRef?>) -> T): T {
    val standard = mapOf(
      kSecAttrService to CFBridgingRetain(serviceName),
      kSecAttrAccessGroup to CFBridgingRetain(null)
    )
    val custom = arrayOf(*values).map { CFBridgingRetain(it) }
    return block.invoke(Context(standard), custom).apply {
      standard.values.plus(custom).forEach { CFBridgingRelease(it) }
    }
  }

  private fun String.toNSData(): NSData? =
    NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)

  private val NSData.stringValue: String
    get() = NSString.create(this, NSUTF8StringEncoding) as String

  private fun OSStatus.validate(): Boolean {
    return (this.toUInt() == noErr)
  }

  private fun generateRandomByteArray(length: Int): ByteArray {
    val byteArray = ByteArray(length)
    arc4random_buf(byteArray.refTo(0), byteArray.size.convert())
    return byteArray
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

  private fun KCryptKeychainEntity.toNsData(): NSData {
    val json = Json { isLenient = true; ignoreUnknownKeys = true }
    return NSString.create(string = json.encodeToString(KCryptKeychainEntity.serializer(), this))
      .dataUsingEncoding(NSUTF8StringEncoding) ?: NSData()
  }

  private fun deSerialize(data: String): KCryptKeychainEntity {
    val json = Json { isLenient = true; ignoreUnknownKeys = true }
    return json.decodeFromString(KCryptKeychainEntity.serializer(), data)
  }

}


actual fun getKCrypt(): KCrypt = KCryptIos()