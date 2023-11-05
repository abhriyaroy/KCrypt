package studio.zebro.kcrypt

import KCryptEntity
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.migration.AutomaticSchemaMigration
import io.realm.kotlin.migration.RealmMigration
import studio.zebro.kcrypt.entity.KCryptStorageItemEntity
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

class KCryptAndroid : KCrypt {
  private val keyAlias = "encryption_key"
  private lateinit var keystore: KeyStore
  private var realm: Realm? = null
  private val keystoreProviderName = "AndroidKeyStore"
  private val realmName = "krypt.realm"

  override fun getEncryptionKey(keySize: Int): ByteArray? {
    initializeKeystore()
    return if (keystore.containsAlias(keyAlias)) {
      hexStringToByteArray(
        decryptDataAsymmetric(keyAlias,
          getEncodedKey().let {
            if (it.isStringInHex) {
              it.encodedKey
            } else {
              stringToHex(it.encodedKey)
            }
          })
      )
    } else {
      generateSymmetricKey(keyAlias)
      val cipherKey = generate64ByteByteArray(keySize)
      saveEncodedKey(encryptDataAsymmetric(keyAlias, byteArrayToHexString(cipherKey)), true)
      cipherKey
    }
  }

  override fun getEncryptionKeyToHexString(keySize: Int): String {
    return getEncryptionKey(keySize)?.let {
      println("saved final $it")
      byteArrayToHexString(it)
    } ?: "NA"
  }

  override fun saveEncryptionKey(key: ByteArray) {
    initializeKeystore()
    generateSymmetricKey(keyAlias)
    saveEncodedKey(encryptDataAsymmetric(keyAlias, byteArrayToHexString(key)), false)
  }

  override fun saveEncryptionKey(key: String, isHexString: Boolean) {
    initializeKeystore()
    generateSymmetricKey(keyAlias)
    saveEncodedKey(
      encryptDataAsymmetric(
        keyAlias,
        if (isHexString) {
          key
        } else {
          stringToHex(key)
        },
      ),
      isHexString = true
    )
  }

  override fun byteArrayToHexString(byteArray: ByteArray): String {
    return byteArray.joinToString("") { "%02x".format(it) }
  }

  override fun hexStringToByteArray(hexString: String): ByteArray? {
    val cleanHexString = hexString.replace(" ", "") // Remove spaces if present
    val byteArray = ByteArray(cleanHexString.length / 2)

    try {
      for (i in cleanHexString.indices step 2) {
        val byteValue = cleanHexString.substring(i, i + 2).toInt(16)
        byteArray[i / 2] = byteValue.toByte()
      }
      return byteArray
    } catch (e: NumberFormatException) {
      // Handle invalid hex string format
      e.printStackTrace()
      return null
    }
  }

  override fun saveString(key: String, value: String) {
    initializeKeystore()
    encryptDataAsymmetric(
      keyAlias,
      stringToHex(value)
    ).run {
      getRealm().writeBlocking {
        copyToRealm(KCryptStorageItemEntity().apply {
          println("save primary $key")
          this.key = key
          this.value = this@run
        }, updatePolicy = UpdatePolicy.ALL)
      }
    }
  }

  override fun getString(key: String): String? {
    initializeKeystore()
    return decryptDataAsymmetric(keyAlias,
      getRealm().query(KCryptStorageItemEntity::class).find().filter { it.key == key }.let {
        println("get saved key value $this")
        it.first().value as String
      }).let {
      hexStringToNormalString(it)
    }
  }

  override fun saveBoolean(key: String, value: Boolean) {
    saveString(key, value.toString())
  }

  override fun getBoolean(key: String): Boolean? {
    return getString(key)?.let {
      it.equals("true", true)
    }
  }

  override fun saveDouble(key: String, value: Double) {
    saveString(key, value.toString())
  }

  override fun getDouble(key: String): Double? {
    return getString(key)?.toDouble()
  }

  override fun saveFloat(key: String, value: Float) {
    saveString(key, value.toString())
  }

  override fun getFloat(key: String): Float? {
    return getString(key)?.toFloat()
  }

  override fun saveInt(key: String, value: Int) {
    return saveString(key, value.toString())
  }

  override fun getInt(key: String): Int? {
    return getString(key)?.toInt()
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

    Exception().printStackTrace()
    println("the data is $data")

    val parts = data.split(",")

    val plainTextByteArray = Base64.getDecoder().decode(parts[0])

    val iv = Base64.getDecoder().decode(parts[1])

    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    val cipherText = cipher.doFinal(plainTextByteArray)

    return cipherText.toString(Charset.defaultCharset()).apply {
      println("got saved key $this")
    }
  }

  private fun saveEncodedKey(key: String, isHexString: Boolean) {
    getRealm().writeBlocking {
      delete(KCryptEntity::class)
      copyToRealm(KCryptEntity().apply {
        println("save $key")
        encodedKey = key
        isStringInHex = isHexString
      })
    }
  }

  private fun getEncodedKey(): KCryptEntity {
    return getRealm().query(KCryptEntity::class).find().first().apply {
      println("get saved key $this")
    }
  }

  private fun getRealm(): Realm {
    if (realm == null) {
      println("migration -111")

      val configuration = RealmConfiguration
        .Builder(
          schema = setOf(
            KCryptEntity::class,
            KCryptStorageItemEntity::class
          )
        )
        .schemaVersion(2)
        .migration(object : AutomaticSchemaMigration {
          override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {

          }
        })
        .name(realmName)
        .build()
      realm = Realm.open(configuration)
    }
    return realm!!
  }

  private fun generate64ByteByteArray(keySize: Int): ByteArray {
    val secureRandom = SecureRandom()
    val byteArray = ByteArray(keySize)
    secureRandom.nextBytes(byteArray)
    return byteArray
  }

  private fun stringToHex(input: String): String {
    val stringBuilder = StringBuilder()
    for (char in input) {
      val hexString = Integer.toHexString(char.toInt())
      stringBuilder.append(hexString)
    }
    return stringBuilder.toString()
      .apply {
        println("the value to save is $this")
      }
  }

  private fun hexStringToNormalString(hexString: String): String {
    val hexChars = hexString.toCharArray()
    val byteArray = ByteArray(hexChars.size / 2)

    for (i in hexChars.indices step 2) {
      val firstDigit = Character.digit(hexChars[i], 16)
      val secondDigit = Character.digit(hexChars[i + 1], 16)

      if (firstDigit == -1 || secondDigit == -1) {
        throw IllegalArgumentException("Invalid hex string")
      }

      val combinedByte = (firstDigit shl 4) + secondDigit
      byteArray[i / 2] = combinedByte.toByte()
    }

    return String(byteArray, Charsets.UTF_8)
  }

}

actual fun getKCrypt(): KCrypt = KCryptAndroid()
