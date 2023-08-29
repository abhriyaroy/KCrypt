package studio.zebro.kcrypt

import KCryptEntity
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class KCryptAndroid : KCrypt {
  private val keyAlias = "encryption_key"
  private lateinit var keystore: KeyStore
  private var realm: Realm? = null
  private val keystoreProviderName = "AndroidKeyStore"

  override fun getEncryptionKey(): String {
    initializeKeystore()
    return if (keystore.containsAlias(keyAlias)) {
      decryptDataAsymmetric(keyAlias, getEncodedKey())
    } else {
      generateSymmetricKey(keyAlias)
      val cipherKey = UUID.randomUUID().toString()
      saveEncodedKey(encryptDataAsymmetric(keyAlias, cipherKey))
      cipherKey
    }


  }

  private fun initializeKeystore(): KeyStore {
    keystore = KeyStore.getInstance(keystoreProviderName)
    keystore.load(null)
    return keystore
  }

  private fun generateSymmetricKey(alias: String): Key {
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

  private fun getAsymmetricKey(alias: String): Key {
    return keystore.getKey(alias, CharArray(0))
  }

  private fun encryptDataAsymmetric(alias: String, data: String): String {
    val key = getAsymmetricKey(alias)
    val plainTextByteArray = data.toByteArray(Charset.defaultCharset())

    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val cipherText = cipher.doFinal(plainTextByteArray)

    return Base64.getEncoder()
      .encodeToString(cipherText) +
        "," +
        Base64.getEncoder().encodeToString(cipher.iv)
  }

  private fun decryptDataAsymmetric(alias: String, data: String): String {
    val key = getAsymmetricKey(alias)

    val parts = data.split(",")

    val plainTextByteArray = Base64.getDecoder().decode(parts[0])

    val iv = Base64.getDecoder().decode(parts[1])

    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    val cipherText = cipher.doFinal(plainTextByteArray)

    return cipherText.toString(Charset.defaultCharset())
  }

  private fun saveEncodedKey(key: String) {
    getRealm().writeBlocking {
      copyToRealm(KCryptEntity().apply {
        encodedKey = key
      })
    }
  }

  private fun getEncodedKey(): String {
    return getRealm().query(KCryptEntity::class).find().first().encodedKey
  }

  private fun getRealm(): Realm {
    if (realm == null) {
      val configuration = RealmConfiguration.create(schema = setOf(KCryptEntity::class))
      realm = Realm.open(configuration)
    }
    return realm!!
  }
}

actual fun getKCrypt(): KCrypt = KCryptAndroid()
