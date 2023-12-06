package studio.zebro.kcrypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.KeyGenerator

interface KeyStoreManager {

  fun initializeKeystore()
  fun containsAlias(alias: String): Boolean
  fun generateSymmetricKey(alias: String): Key
  fun getAsymmetricKey(alias: String): Key
  fun generate64ByteByteArray(keySize: Int): ByteArray
}

class KeyStoreManagerImpl : KeyStoreManager {
  private lateinit var keystore: KeyStore
  private val keystoreProviderName = "AndroidKeyStore"

  override fun initializeKeystore() {
    keystore = KeyStore.getInstance(keystoreProviderName)
    keystore.load(null)
  }

  override fun containsAlias(alias: String): Boolean = keystore.containsAlias(alias)

  override fun generateSymmetricKey(alias: String): Key {
    var existingKey: Key? = null
    try {
      existingKey = getAsymmetricKey(alias)
    } catch (exception: NullPointerException) {

    }
    if (existingKey != null) {
      return existingKey
    }

    // Specify the algorithm to be used
    val generator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES,
      keystoreProviderName
    )
    val generatorSpec = KeyGenParameterSpec.Builder(
      alias,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
      .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

    generator.init(generatorSpec.build())
    return generator.generateKey()
  }

  override fun getAsymmetricKey(alias: String): Key {
    return keystore.getKey(alias, CharArray(0))
  }

  override fun generate64ByteByteArray(keySize: Int): ByteArray {
    val secureRandom = SecureRandom()
    val byteArray = ByteArray(keySize)
    secureRandom.nextBytes(byteArray)
    return byteArray
  }

}