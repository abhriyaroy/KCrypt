package studio.zebro.kcrypt

import kotlinx.cinterop.memScoped
import platform.Foundation.NSString
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.*
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.darwin.OSStatus
import platform.darwin.noErr
import platform.Foundation.NSUUID
import platform.posix.arc4random_buf

class KCryptIos : KCrypt {

  val serviceName: String = "studio.zebro.kcrypt"
  val keyName: String = "KCryptKey"

  val accessibility: Accessible = Accessible.WhenUnlocked

  enum class Accessible(val value: CFStringRef?) {
    WhenPasscodeSetThisDeviceOnly(kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly),
    WhenUnlockedThisDeviceOnly(kSecAttrAccessibleWhenUnlockedThisDeviceOnly),
    WhenUnlocked(kSecAttrAccessibleWhenUnlocked),
    AfterFirstUnlock(kSecAttrAccessibleAfterFirstUnlock),
    AfterFirstUnlockThisDeviceOnly(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
  }

  override fun getEncryptionKey(keySize : Int): ByteArray? {
    val preStoredEncryptionKey = value(forKey = keyName)
    return if (preStoredEncryptionKey != null) {
      println("pre stored key available")
      preStoredEncryptionKey.stringValue?.let {
        hexStringToByteArray(it)
      }
    } else {
      println("pre stored key not available")
      val newEncryptionKey = generateRandomByteArray(keySize)
      addOrUpdate(keyName, byteArrayToHexString(newEncryptionKey).toNSData())
      newEncryptionKey
    }
  }

  override fun getEncryptionKeyToHexString(keySize: Int): String {
    return getEncryptionKey(keySize)?.let {
      byteArrayToHexString(it)
    } ?: "NA"
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

  private fun addOrUpdate(key: String, value: NSData?): Boolean {
    return if (existsObject(key)) {
      println("addOrUpdate: update")
      update(key, value)
    } else {
      println("addOrUpdate: add")
      add(key, value)
    }
  }

  private fun add(key: String, value: NSData?): Boolean = context(key, value) { (account, data) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecValueData to data,
      kSecAttrAccessible to accessibility.value
    )
    println("query: ${query.toString()}")
    SecItemAdd(query, null)
      .validate()
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

    SecItemUpdate(query, updateQuery)
      .validate()
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

    SecItemCopyMatching(query, null)
      .validate()
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

  private val NSData.stringValue: String?
    get() = NSString.create(this, NSUTF8StringEncoding) as String?

  private fun OSStatus.validate(): Boolean {
    println("status: $this")
    return (this.toUInt() == noErr).apply {
      if (!this) println("Error: $this")
    }
  }

  private fun generateRandomByteArray(length : Int): ByteArray {
    val byteArray = ByteArray(length)
    arc4random_buf(byteArray.refTo(0), byteArray.size.convert())
    return byteArray
  }

}


actual fun getKCrypt(): KCrypt = KCryptIos()