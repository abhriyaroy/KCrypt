package studio.zebro.kcrypt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.serialization.json.Json
import platform.CoreFoundation.CFAutorelease
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccessGroup
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus
import studio.zebro.kcrypt.entity.KCryptKeychainEntity

interface KeystoreManager {
  fun existsObject(forKey: String): Boolean

  fun add(key: String, value: KCryptKeychainEntity): Boolean
  fun add(key: String, value: String): Boolean

  fun update(key: String, value: KCryptKeychainEntity): Boolean
  fun update(key: String, value: String): Boolean

  fun value(forKey: String): String?
}

@OptIn(ExperimentalForeignApi::class)
class KeystoreManagerImpl : KeystoreManager {

  val serviceName: String = "studio.zebro.kcrypt"

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

  override fun existsObject(forKey: String): Boolean = context(forKey) { (account) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecReturnData to kCFBooleanFalse,
    )

    SecItemCopyMatching(query, null).validate()
  }

  override fun add(key: String, value: KCryptKeychainEntity) =
    context(key, value.toNsData()) { (account, data) ->
      val query = query(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to account,
        kSecValueData to data,
        kSecAttrAccessible to accessibility.value
      )
      SecItemAdd(query, null).validate()
    }

  override fun add(key: String, value: String): Boolean =
    context(key, value.toNSData()) { (account, data) ->
      val query = query(
        kSecClass to kSecClassGenericPassword,
        kSecAttrAccount to account,
        kSecValueData to data,
        kSecAttrAccessible to accessibility.value
      )
      SecItemAdd(query, null).validate()
    }

  override fun update(key: String, value: KCryptKeychainEntity): Boolean =
    context(key, value.toNsData()) { (account, data) ->
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

  override fun update(key: String, value: String): Boolean =
    context(key, value.toNSData()) { (account, data) ->
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

  override fun value(forKey: String): String? = context(forKey) { (account) ->
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
  }.let {
    it?.stringValue
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

  private fun OSStatus.validate(): Boolean {
    return (this.toUInt() == platform.darwin.noErr)
  }

  private fun String.toNSData(): NSData? =
    NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)

  private val NSData.stringValue: String
    get() = NSString.create(this, NSUTF8StringEncoding) as String

  private fun KCryptKeychainEntity.toNsData(): NSData {
    val json = Json { isLenient = true; ignoreUnknownKeys = true }
    return NSString.create(string = json.encodeToString(KCryptKeychainEntity.serializer(), this))
      .dataUsingEncoding(NSUTF8StringEncoding) ?: NSData()
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
}
