package studio.zebro.kcrypt

import KCryptEntity
import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KCryptAndroid : KCrypt {
  private val keyAlias = "encryption_key"
  private lateinit var keystore: KeyStore
  private val prefsKeyAlias = "storedKey"
  private val prefsName = "KCryptPrefs"
  private var realm: Realm? = null

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
    // There could be many keystore providers.
    // We are interested in AndroidKeyStore
    keystore = KeyStore.getInstance("AndroidKeyStore")
    keystore.load(null)
    return keystore
  }

  private fun generateSymmetricKey(alias: String): Key {
    // Specify the algorithm to be used
    val generator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES,
      "AndroidKeyStore"
    )
    // Configurations for the key
    val generatorSpec = KeyGenParameterSpec.Builder(
      alias,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
      .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

    generator.init(generatorSpec.build())
    // Generate the key
    return generator.generateKey()
  }

  // Retrieve the reference to the key by passing the same alias
// that was used while creating
  private fun getAsymmetricKey(alias: String): Key {
    return keystore.getKey(alias, CharArray(0))
  }

  private fun encryptDataAsymmetric(alias: String, data: String): String {
    var key = getAsymmetricKey(alias)
    var plainTextByteArray = data.toByteArray(Charset.defaultCharset())

    var cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    var cipherText = cipher.doFinal(plainTextByteArray)

    // IV needs to be preserved which will be used during decryption
    // Encode cipher text and the iv to Base64 format
    // Concatenate both strings separated by a comma(,)
    return Base64.getEncoder()
      .encodeToString(cipherText) +
        "," +
        Base64.getEncoder().encodeToString(cipher.iv)
  }

  private fun decryptDataAsymmetric(alias: String, data: String): String {
    var key = getAsymmetricKey(alias)

    // Extract the cipher text and the IV
    var parts = data.split(",")

    // Base64 decode of cipher text
    var plainTextByteArray = Base64.getDecoder().decode(parts[0])

    // Base64 decode of the IV
    var iv = Base64.getDecoder().decode(parts[1])

    var cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    var cipherText = cipher.doFinal(plainTextByteArray)

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
