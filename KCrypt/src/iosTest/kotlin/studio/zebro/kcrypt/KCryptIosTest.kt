package studio.zebro.kcrypt

import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KCryptIosTest {

  private lateinit var kCryptIos: KCryptIos

  @Mock
  private val mockKeystoreManager = mock(classOf<KeystoreManager>())

  @BeforeTest
  fun setUp() {
    kCryptIos = KCryptIos(IosPlatformHelperImpl(), mockKeystoreManager)
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
}
