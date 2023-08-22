package studio.zebro.kcrypt

import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.*

class KCryptIos : KCrypt {
  private val serviceIdentifier = "studio.zebro.kcrypt.key"

  override fun getEncryptionKey(): String {
//    val query = mapOf(
//      kSecClass to kSecClassKey,
//      kSecAttrService to serviceIdentifier,
//      kSecAttrSynchronizable to kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
//      kSecReturnData to true
//    )
//    val result: NSData? = memScoped {
//      val nsResult = alloc<CFTypeRefVar>()
//      SecItemCopyMatching(query, nsResult.ptr)
//      nsResult.value as? NSData
//    }
//    return result?.bytes?.readBytes(result.length.toInt())
    return "null"
  }

  private fun saveEncryptionKey(key: ByteArray) {
//    val query = mapOf(
//      kSecClass to kSecClassKey,
//      kSecAttrService to serviceIdentifier,
//      kSecAttrSynchronizable to kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
//      kSecValueData to NSData.create(key)
//    )
//    val status = SecItemAdd(query, null)
//    if (status != 0u) {
//      // Handle error
//    }
  }
}

actual fun getKCrypt(): KCrypt = KCryptIos()