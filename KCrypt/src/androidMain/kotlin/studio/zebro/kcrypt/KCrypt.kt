package studio.zebro.kcrypt

import KCryptEntity
import studio.zebro.kcrypt.entity.KCryptStorageItemEntity
import java.nio.charset.Charset
import java.security.SecureRandom

class KCryptAndroid(
  private val keyStoreManager: KeyStoreManager,
  private val cipherProvider: CipherProvider,
  private val storageProvider: StorageProvider
) : KCrypt {

  private val keyAlias = "encryption_key"

  override fun getEncryptionKey(keySize: Int): ByteArray? {
    println("$00")
    keyStoreManager.initializeKeystore()
    println("$01")
    return if (keyStoreManager.containsAlias(keyAlias)) {
      hexStringToByteArray(
        decryptDataAsymmetric(keyAlias, getEncodedKey().let {
          if (it.isStringInHex) {
            it.encodedKey
          } else {
            stringToHex(it.encodedKey)
          }
        })
      )
    } else {
      println("$10")
      keyStoreManager.generateSymmetricKey(keyAlias)
      println("$11")
      val cipherKey = generate64ByteByteArray(keySize)
      println("cipher key = ${cipherKey.size}")
      saveEncodedKey(encryptDataAsymmetric(keyAlias, byteArrayToHexString(cipherKey)), true)
      cipherKey
    }
  }

  override fun getEncryptionKeyToHexString(keySize: Int): String {
    return getEncryptionKey(keySize)?.let {
      byteArrayToHexString(it)
    } ?: "NA"
  }

  override fun saveEncryptionKey(key: ByteArray) {
    keyStoreManager.initializeKeystore()
    keyStoreManager.generateSymmetricKey(keyAlias)
    saveEncodedKey(encryptDataAsymmetric(keyAlias, byteArrayToHexString(key)), false)
  }

  override fun saveEncryptionKey(key: String, isHexString: Boolean) {
    keyStoreManager.initializeKeystore()
    keyStoreManager.generateSymmetricKey(keyAlias)
    saveEncodedKey(
      encryptDataAsymmetric(
        keyAlias,
        if (isHexString) {
          key
        } else {
          stringToHex(key)
        },
      ), isHexString = true
    )
  }

  override fun byteArrayToHexString(byteArray: ByteArray): String {
    return byteArray.joinToString("") { "%02x".format(it) }
  }

  override fun hexStringToByteArray(hexString: String): ByteArray? {
    val cleanHexString = hexString.replace(" ", "")
    val byteArray = ByteArray(cleanHexString.length / 2)

    return try {
      for (i in cleanHexString.indices step 2) {
        val byteValue = cleanHexString.substring(i, i + 2).toInt(16)
        byteArray[i / 2] = byteValue.toByte()
      }
      byteArray
    } catch (e: NumberFormatException) {
      // Handle invalid hex string format
      e.printStackTrace()
      null
    }
  }

  override fun saveString(key: String, value: String) {
    keyStoreManager.initializeKeystore()
    encryptDataAsymmetric(
      keyAlias, stringToHex(value)
    ).run {
      storageProvider.writeItemToStorage(KCryptStorageItemEntity().apply {
        this.key = key
        this.value = this@run
      })
    }
  }

  override fun getString(key: String): String? {
    keyStoreManager.initializeKeystore()
    return decryptDataAsymmetric(
      keyAlias, storageProvider.getItemFromStorage(key)
    ).let {
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

  override fun saveLong(key: String, value: Long) {
    return saveString(key, value.toString())
  }

  override fun getLong(key: String): Long? {
    return getString(key)?.toLong()
  }

  private fun encryptDataAsymmetric(alias: String, data: String): String {
    val key = keyStoreManager.getAsymmetricKey(alias)
    val plainTextByteArray = data.toByteArray(Charset.defaultCharset())
    return cipherProvider.encrypt(plainTextByteArray, key)
  }

  private fun decryptDataAsymmetric(alias: String, data: String): String {
    val key = keyStoreManager.getAsymmetricKey(alias)

    val cipherText = cipherProvider.decrypt(key, data)

    return cipherText.toString(Charset.defaultCharset())
  }

  private fun saveEncodedKey(key: String, isHexString: Boolean) {
    println("$key and $isHexString")
    storageProvider.writeKey(KCryptEntity().apply {
      encodedKey = key
      isStringInHex = isHexString
    })
  }

  private fun getEncodedKey(): KCryptEntity {
    return storageProvider.getKey()
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

var kCryptInstance: KCrypt? = null
actual fun getKCrypt(): KCrypt =
  kCryptInstance ?: KCryptAndroid(
    KeyStoreManagerImpl(), CipherProviderImpl(), StorageProviderImpl()
  ).apply {
    kCryptInstance = this
  }
