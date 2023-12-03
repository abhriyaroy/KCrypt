package studio.zebro.kcrypt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
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
import platform.darwin.noErr

interface KeystoreManager {
  fun existsObject(forKey: String): Boolean

  fun add(key: String, value: NSData?): Boolean

  fun update(key: String, value: Any?): Boolean

  fun value(forKey: String): NSData?
}

@OptIn(ExperimentalForeignApi::class)
class KeystoreManagerImpl  : KeystoreManager {

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

  override fun add(key: String, value: NSData?): Boolean = context(key, value) { (account, data) ->
    val query = query(
      kSecClass to kSecClassGenericPassword,
      kSecAttrAccount to account,
      kSecValueData to data,
      kSecAttrAccessible to accessibility.value
    )
    SecItemAdd(query, null).validate()
  }

  override fun update(key: String, value: Any?): Boolean = context(key, value) { (account, data) ->
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

  override fun value(forKey: String): NSData? = context(forKey) { (account) ->
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
