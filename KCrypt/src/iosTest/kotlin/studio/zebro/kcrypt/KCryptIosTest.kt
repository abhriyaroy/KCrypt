package studio.zebro.kcrypt

import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import kotlinx.serialization.json.Json
import studio.zebro.kcrypt.entity.KCryptKeychainEntity
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KCryptIosTest {

  private lateinit var kCryptIos: KCryptIos

  @Mock
  private val mockKeystoreManager = mock(classOf<KeystoreManager>())

  @Mock
  private val mockIosPlatformHelper = mock(classOf<IosPlatformHelper>())

  @BeforeTest
  fun setUp() {
    kCryptIos = KCryptIos(mockIosPlatformHelper, mockKeystoreManager)
  }

  @Test
  fun `saveString saves string correctly in keystore when it doesnt exist`() {
    val testKey = "testKey"
    val testValue = "testValue"

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue)
    }.returns(true)

    kCryptIos.saveString(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue)
    }.wasNotInvoked()
  }

  @Test
  fun `saveString saves string correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = "testValue"

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue)
    }.returns(true)

    kCryptIos.saveString(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue)
    }.wasNotInvoked()
  }

  @Test
  fun `saveBoolean saves boolean correctly in keystore when it doesn't exist`() {
    val testKey = "testKey"
    val testValue = true

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveBoolean(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveBoolean saves boolean correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = true

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveBoolean(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveInt saves integer correctly in keystore when it doesn't exist`() {
    val testKey = "testKey"
    val testValue = 123

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveInt(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveInt saves integer correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = 123

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveInt(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveLong saves long correctly in keystore when it doesn't exist`() {
    val testKey = "testKey"
    val testValue = 123456789L

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveLong(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveLong saves long correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = 123456789L

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveLong(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveFloat saves float correctly in keystore when it doesn't exist`() {
    val testKey = "testKey"
    val testValue = 123.45f

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveFloat(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveFloat saves float correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = 123.45f

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveFloat(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveDouble saves double correctly in keystore when it doesn't exist`() {
    val testKey = "testKey"
    val testValue = 123.456

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(false)
    every {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveDouble(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `saveDouble saves double correctly in keystore when it exists`() {
    val testKey = "testKey"
    val testValue = 123.456

    every {
      mockKeystoreManager.existsObject(testKey)
    }.returns(true)
    every {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.returns(true)

    kCryptIos.saveDouble(testKey, testValue)

    verify {
      mockKeystoreManager.existsObject(testKey)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.update(testKey, testValue.toString())
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(testKey, testValue.toString())
    }.wasNotInvoked()
  }

  @Test
  fun `getString retrieves string correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "expectedStringValue"

    // Setup mock behavior
    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    // Execute the method under test
    val result = kCryptIos.getString(testKey)

    // Verify if the correct string was retrieved
    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(expectedValue, result)
  }

  @Test
  fun `getBoolean retrieves boolean correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "true"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getBoolean(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(true, result)
  }

  @Test
  fun `getBoolean retrieves boolean correctly from keystore with value false`() {
    val testKey = "testKey"
    val expectedValue = "false"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getBoolean(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(false, result)
  }

  @Test
  fun `getInt retrieves integer correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "123"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getInt(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(123, result)
  }

  @Test
  fun `getLong retrieves long correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "123456789"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getLong(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(123456789L, result)
  }

  @Test
  fun `getFloat retrieves float correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "123.45"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getFloat(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(123.45f, result)
  }

  @Test
  fun `getDouble retrieves double correctly from keystore`() {
    val testKey = "testKey"
    val expectedValue = "123.456"

    every {
      mockKeystoreManager.value(testKey)
    }.returns(expectedValue)

    val result = kCryptIos.getDouble(testKey)

    verify {
      mockKeystoreManager.value(testKey)
    }.wasInvoked(1)

    assertEquals(123.456, result)
  }

  @Test
  fun `getEncryptionKey retrieves and decodes pre-stored hex key correctly`() {
    val testKeyName = "KCryptKey"
    val testHexString = "A1B2C3" // Example hex string
    val expectedByteArray =
      byteArrayOf(0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte()) // Corresponding ByteArray
    val testKCryptKeychainEntity = KCryptKeychainEntity(testHexString, true)

    every {
      mockKeystoreManager.value(testKeyName)
    }.returns(Json.encodeToString(KCryptKeychainEntity.serializer(), testKCryptKeychainEntity))

    val result = kCryptIos.getEncryptionKey(expectedByteArray.size)

    verify {
      mockKeystoreManager.value(testKeyName)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(any(), any<KCryptKeychainEntity>())
    }.wasNotInvoked()
    verify {
      mockKeystoreManager.update(any(), any<KCryptKeychainEntity>())
    }.wasNotInvoked()
    assertEquals(expectedByteArray.toList(), result?.toList())
  }

  @Test
  fun `getEncryptionKey retrieves and converts and decodes pre-stored non-hex key correctly`() {
    val testKeyName = "KCryptKey"
    val testString = "TestKey" // Example string
    val expectedByteArray = testString.encodeToByteArray() // Corresponding ByteArray
    val testKCryptKeychainEntity = KCryptKeychainEntity(testString, false)

    every {
      mockKeystoreManager.value(testKeyName)
    }.returns(Json.encodeToString(KCryptKeychainEntity.serializer(), testKCryptKeychainEntity))

    val result = kCryptIos.getEncryptionKey(expectedByteArray.size)

    verify {
      mockKeystoreManager.value(testKeyName)
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add(any(), any<KCryptKeychainEntity>())
    }.wasNotInvoked()
    verify {
      mockKeystoreManager.update(any(), any<KCryptKeychainEntity>())
    }.wasNotInvoked()
    assertEquals(expectedByteArray.toList(), result?.toList())
  }

  @Test
  fun `getEncryptionKey generates stores and returns new key when none is pre-stored`() {
    val keySize = 16 // Example key size
    val generatedKey = ByteArray(keySize) { it.toByte() } // Example generated key
    val expectedHexString = byteArrayToHexString(generatedKey)

    every {
      mockKeystoreManager.value("KCryptKey")
    }.returns(null)

    every {
      mockKeystoreManager.existsObject("KCryptKey")
    }.returns(false)

    every {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(expectedHexString, true))
    }.returns(true)

    every {
      mockIosPlatformHelper.generateRandomByteArray(keySize)
    }.returns(generatedKey)

    val result = kCryptIos.getEncryptionKey(keySize)

    verify {
      mockKeystoreManager.value("KCryptKey")
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.existsObject("KCryptKey")
    }.wasInvoked(1)
    verify {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(expectedHexString, true))
    }.wasInvoked(1)
    verify {
      mockIosPlatformHelper.generateRandomByteArray(keySize)
    }.wasInvoked(1)
    assertEquals(generatedKey.toList(), result?.toList())
  }

  @Test
  fun `getEncryptionKeyToHexString returns existing hex key correctly`() {
    val testHexString = "A1B2C3" // Example hex string
    val testKCryptKeychainEntity = KCryptKeychainEntity(testHexString, true)

    every {
      mockKeystoreManager.value("KCryptKey")
    }.returns(Json.encodeToString(KCryptKeychainEntity.serializer(), testKCryptKeychainEntity))

    val result = kCryptIos.getEncryptionKeyToHexString(3)

    assertEquals(testHexString, result)
    verify {
      mockIosPlatformHelper.generateRandomByteArray(any())
    }.wasNotInvoked()
  }

  @Test
  fun `getEncryptionKeyToHexString returns existing non-hex key as hex correctly`() {
    val testString = "TestKey"
    val expectedHexString = byteArrayToHexString(testString.encodeToByteArray())
    val testKCryptKeychainEntity = KCryptKeychainEntity(testString, false)

    every {
      mockKeystoreManager.value("KCryptKey")
    }.returns(Json.encodeToString(KCryptKeychainEntity.serializer(), testKCryptKeychainEntity))

    val result = kCryptIos.getEncryptionKeyToHexString(testString.length)

    assertEquals(expectedHexString, result)
    verify {
      mockIosPlatformHelper.generateRandomByteArray(any())
    }.wasNotInvoked()
  }

  @Test
  fun `getEncryptionKeyToHexString generates and returns new key as hex when no key is available`() {
    val keySize = 16
    val generatedKey = ByteArray(keySize) { it.toByte() }
    val expectedHexString = byteArrayToHexString(generatedKey)

    every {
      mockKeystoreManager.value("KCryptKey")
    }.returns(null)
    every {
      mockIosPlatformHelper.generateRandomByteArray(keySize)
    }.returns(generatedKey)
    every {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(expectedHexString, true))
    }.returns(true)
    every {
      mockKeystoreManager.existsObject("KCryptKey")
    }.returns(false)

    val result = kCryptIos.getEncryptionKeyToHexString(keySize)

    assertEquals(expectedHexString, result)
    verify {
      mockKeystoreManager.value("KCryptKey")
    }.wasInvoked(1)
    verify {
      mockIosPlatformHelper.generateRandomByteArray(keySize)
    }.wasInvoked(1)
  }

  @Test
  fun `saveEncryptionKey saves hex string key correctly`() {
    val testKey = "A1B2C3"
    val isHexString = true

    every {
      mockKeystoreManager.existsObject("KCryptKey")
    }.returns(false)
    every {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(testKey, isHexString))
    }.returns(true)

    kCryptIos.saveEncryptionKey(testKey, isHexString)

    verify {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(testKey, isHexString))
    }.wasInvoked(1)
  }

  @Test
  fun `saveEncryptionKey saves non-hex string key correctly`() {
    val testKey = "TestKey"
    val isHexString = false

    every {
      mockKeystoreManager.existsObject("KCryptKey")
    }.returns(false)
    every {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(testKey, isHexString))
    }.returns(true)

    kCryptIos.saveEncryptionKey(testKey, isHexString)

    verify {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(testKey, isHexString))
    }.wasInvoked(1)
  }

  @Test
  fun `saveEncryptionKey saves byte array key correctly`() {
    val testKey = ByteArray(16) { it.toByte() }
    val expectedHexString = byteArrayToHexString(testKey)

    every {
      mockKeystoreManager.existsObject("KCryptKey")
    }.returns(false)
    every {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(expectedHexString, true))
    }.returns(true)

    kCryptIos.saveEncryptionKey(testKey)

    verify {
      mockKeystoreManager.add("KCryptKey", KCryptKeychainEntity(expectedHexString, true))
    }.wasInvoked(1)
  }

  @Test
  fun `test byteArrayToHexString to return proper string when called successfully`() {
    val testKey = ByteArray(16) { (it * 2).toByte() }

    assertEquals(byteArrayToHexString(testKey), kCryptIos.byteArrayToHexString(testKey))
  }

  private fun byteArrayToHexString(byteArray: ByteArray): String {
    val hexChars = "0123456789ABCDEF".toCharArray()
    val result = StringBuilder()
    println("mock == $byteArray")
    for (byte in byteArray) {
      val highNibble = (byte.toInt() ushr 4) and 0x0F
      val lowNibble = byte.toInt() and 0x0F
      result.append(hexChars[highNibble])
      result.append(hexChars[lowNibble])
    }
    println("mock2 == $byteArray")
    return result.toString()
  }

}
