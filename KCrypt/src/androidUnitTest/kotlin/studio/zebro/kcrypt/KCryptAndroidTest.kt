package studio.zebro.kcrypt

import KCryptEntity
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import studio.zebro.kcrypt.entity.KCryptStorageItemEntity
import java.nio.charset.Charset
import java.security.Key

class KCryptAndroidTest {

  @Mock
  private lateinit var mockCipherProvider: CipherProvider

  @Mock
  private lateinit var mockKeyStoreManager: KeyStoreManager

  @Mock
  private lateinit var mockStorageProvider: StorageProvider

  @Mock
  private lateinit var mockKey: Key

  private lateinit var kCryptAndroid: KCryptAndroid

  private val keyAlias = "encryption_key"
  private val keySize = 64

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    kCryptAndroid = KCryptAndroid(mockKeyStoreManager, mockCipherProvider, mockStorageProvider)
  }

  @Test
  fun `saveString encrypts and saves string correctly`() {
    val testKey = "testKey"
    val testValue = "testValue"
    val encryptedValue = "jtddMF4vauvoSbiPSlJj4TjodYfKkyuFLsCZksh3B5g=,hrpTOB2QCOcAH33iVEOosA=="

    whenever(
      mockCipherProvider.encrypt(
        eq(stringToHex(testValue).toByteArray(Charset.defaultCharset())),
        any()
      )
    ).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    kCryptAndroid.saveString(testKey, testValue)

    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).getAsymmetricKey(keyAlias)
    verify(mockCipherProvider).encrypt(eq(stringToHex(testValue).toByteArray()), eq(mockKey))
    verify(mockStorageProvider).writeItemToStorage(KCryptStorageItemEntity().apply {
      key = testKey
      value = encryptedValue
    })
  }

  @Test
  fun `saveBoolean encrypts and saves boolean correctly`() {
    val testKey = "testKey"
    val testValue = true
    val encryptedValue = "encryptedBoolean"

    whenever(
      mockCipherProvider.encrypt(
        eq(stringToHex(testValue.toString()).toByteArray(Charset.defaultCharset())),
        any()
      )
    ).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    kCryptAndroid.saveBoolean(testKey, testValue)

    verifyInteractionsForSave(testKey, stringToHex(testValue.toString()), encryptedValue)
  }

  @Test
  fun `saveInt encrypts and saves int correctly`() {
    val testKey = "testKey"
    val testValue = 123
    val encryptedValue = "encryptedInt"

    // Setup mock behavior
    whenever(
      mockCipherProvider.encrypt(
        eq(stringToHex(testValue.toString()).toByteArray(Charset.defaultCharset())),
        any()
      )
    ).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    // Test method
    kCryptAndroid.saveInt(testKey, testValue)

    // Verify method interactions
    verifyInteractionsForSave(testKey, stringToHex(testValue.toString()), encryptedValue)
  }

  @Test
  fun `saveDouble encrypts and saves double correctly`() {
    val testKey = "testKey"
    val testValue = 123.45
    val encryptedValue = "encryptedDouble"

    // Setup mock behavior
    whenever(
      mockCipherProvider.encrypt(
        eq(stringToHex(testValue.toString()).toByteArray(Charset.defaultCharset())),
        any()
      )
    ).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    // Test method
    kCryptAndroid.saveDouble(testKey, testValue)

    // Verify method interactions
    verifyInteractionsForSave(testKey, stringToHex(testValue.toString()), encryptedValue)
  }

  @Test
  fun `saveFloat encrypts and saves float correctly`() {
    val testKey = "testKey"
    val testValue = 123.45f // float value
    val encryptedValue = "encryptedFloat"

    // Setup mock behavior
    whenever(
      mockCipherProvider.encrypt(
        eq(stringToHex(testValue.toString()).toByteArray(Charset.defaultCharset())),
        any()
      )
    ).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    // Test method
    kCryptAndroid.saveFloat(testKey, testValue)

    // Verify method interactions
    verifyInteractionsForSave(testKey, stringToHex(testValue.toString()), encryptedValue)
  }

  @Test
  fun `saveLong encrypts and saves long correctly`() {
    val testKey = "testKey"
    val testValue = 123456789L
    val encryptedValue = "encryptedLong"

    // Setup mock behavior
    whenever(mockCipherProvider.encrypt(eq(stringToHex(testValue.toString()).toByteArray(Charset.defaultCharset())), any())).thenReturn(encryptedValue)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)

    // Test method
    kCryptAndroid.saveLong(testKey, testValue)

    // Verify method interactions
    verifyInteractionsForSave(testKey, stringToHex(testValue.toString()), encryptedValue)
  }

  @Test
  fun `getString retrieves and decrypts string correctly`() {
    val testKey = "testKey"
    val encryptedValue = "jtddMF4vauvoSbiPSlJj4TjodYfKkyuFLsCZksh3B5g=,hrpTOB2QCOcAH33iVEOosA=="
    val decryptedValue = "testValue"

    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    val result = kCryptAndroid.getString(testKey)

    verifyInteractionsForGet(testKey, encryptedValue)

    assert(result == decryptedValue)
  }

  @Test
  fun `getBoolean retrieves and decrypts boolean correctly`() {
    val testKey = "testKey"
    val encryptedValue = "encryptedBoolean"
    val decryptedValue = "true" // or "false"

    // Setup mock behavior
    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    // Test method
    val result = kCryptAndroid.getBoolean(testKey)

    // Verify result
    assert(result == decryptedValue.toBoolean())

    verifyInteractionsForGet(testKey, encryptedValue)
  }

  @Test
  fun `getInt retrieves and decrypts int correctly`() {
    val testKey = "testKey"
    val encryptedValue = "encryptedInt"
    val decryptedValue = "123"

    // Setup mock behavior
    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    // Test method
    val result = kCryptAndroid.getInt(testKey)

    // Verify result
    assert(result == decryptedValue.toInt())

    // Verify method interactions
    verifyInteractionsForGet(testKey, encryptedValue)
  }

  @Test
  fun `getDouble retrieves and decrypts double correctly`() {
    val testKey = "testKey"
    val encryptedValue = "encryptedDouble"
    val decryptedValue = "123.45"

    // Setup mock behavior
    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    // Test method
    val result = kCryptAndroid.getDouble(testKey)

    // Verify result
    assert(result == decryptedValue.toDouble())

    // Verify method interactions
    verifyInteractionsForGet(testKey, encryptedValue)
  }

  @Test
  fun `getFloat retrieves and decrypts float correctly`() {
    val testKey = "testKey"
    val encryptedValue = "encryptedFloat"
    val decryptedValue = "123.45"

    // Setup mock behavior
    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    // Test method
    val result = kCryptAndroid.getFloat(testKey)

    // Verify result
    assert(result == decryptedValue.toFloat())

    // Verify method interactions
    verifyInteractionsForGet(testKey, encryptedValue)
  }

  @Test
  fun `getLong retrieves and decrypts long correctly`() {
    val testKey = "testKey"
    val encryptedValue = "encryptedLong"
    val decryptedValue = "123456789"

    // Setup mock behavior
    setupMocksForGet(testKey, encryptedValue, decryptedValue)

    // Test method
    val result = kCryptAndroid.getLong(testKey)

    // Verify result
    assert(result == decryptedValue.toLong())

    // Verify method interactions
    verifyInteractionsForGet(testKey, encryptedValue)
  }

  @Test
  fun `getEncryptionKey returns key when KeyStoreManager contains alias`() {
    val expectedKey = "encryptedKeyString"
    val expectedKeyByteArray = expectedKey.toByteArray(Charset.defaultCharset())
    val expectedKeyHexString = stringToHex(expectedKey)
    val expectedHexByteArray = expectedKeyHexString.toByteArray(Charset.defaultCharset())
    val kCryptEntity = KCryptEntity().apply {
      encodedKey = expectedKeyHexString
      isStringInHex = true
    }

    whenever(mockKeyStoreManager.containsAlias(keyAlias)).thenReturn(true)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)
    whenever(mockCipherProvider.decrypt(mockKey, expectedKeyHexString)).thenReturn(expectedHexByteArray)
    whenever(mockStorageProvider.getKey()).thenReturn(kCryptEntity)

    val result = kCryptAndroid.getEncryptionKey(64)

    assertNotNull(result)
    assertArrayEquals(expectedKeyByteArray, result)
    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).containsAlias(eq(keyAlias))
    verify(mockKeyStoreManager).getAsymmetricKey(eq(keyAlias))
    verify(mockCipherProvider).decrypt(eq(mockKey), eq(expectedKeyHexString))
    verify(mockStorageProvider).getKey()
  }

  @Test
  fun `getEncryptionKey returns key when KeyStoreManager contains alias and isStringInHex is false`() {
    val rawKeyString = "rawKeyString"
    val rawKeyStringByteArray = rawKeyString.toByteArray(Charset.defaultCharset())
    val rawKeyStringHex = stringToHex("rawKeyString")
    val rawKeyHexByteArray = rawKeyStringHex.toByteArray(Charset.defaultCharset())
    val kCryptEntity = KCryptEntity().apply {
      encodedKey = rawKeyString
      isStringInHex = false
    }

    whenever(mockKeyStoreManager.containsAlias(keyAlias)).thenReturn(true)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)
    whenever(mockCipherProvider.decrypt(mockKey, rawKeyStringHex)).thenReturn(rawKeyHexByteArray)
    whenever(mockStorageProvider.getKey()).thenReturn(kCryptEntity)

    val result = kCryptAndroid.getEncryptionKey(64)

    assertNotNull(result)
    assertArrayEquals(rawKeyStringByteArray, result)
    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).containsAlias(eq(keyAlias))
    verify(mockKeyStoreManager).getAsymmetricKey(eq(keyAlias))
    verify(mockCipherProvider).decrypt(eq(mockKey), eq(rawKeyStringHex))
    verify(mockStorageProvider).getKey()
  }

  @Test
  fun `getEncryptionKey generates new key when KeyStoreManager does not contain alias`() {
    val generatedKey = ByteArray(keySize) // Simulate a generated key
    val generatedKeyToEncryption = byteArrayToHexString(generatedKey).toByteArray(Charset.defaultCharset())
    val encryptedKeyHexString = stringToHex("encryptedKeyHexString")
    whenever(mockKeyStoreManager.containsAlias(keyAlias)).thenReturn(false)
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)
    whenever(mockKeyStoreManager.generateSymmetricKey(keyAlias)).thenReturn(mockKey)
    whenever(mockKeyStoreManager.generate64ByteByteArray(keySize)).thenReturn(generatedKey)
    whenever(mockCipherProvider.encrypt(generatedKeyToEncryption, mockKey)).thenReturn(encryptedKeyHexString)
    whenever(mockStorageProvider.writeKey(any())).thenAnswer { }


    val result = kCryptAndroid.getEncryptionKey(keySize)

    assertNotNull(result)
    assertArrayEquals(generatedKey, result)
    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).containsAlias(keyAlias)
    verify(mockKeyStoreManager).getAsymmetricKey(keyAlias)
    verify(mockKeyStoreManager).generateSymmetricKey(keyAlias)
    verify(mockKeyStoreManager).generate64ByteByteArray(keySize)
    verify(mockCipherProvider).encrypt(generatedKeyToEncryption, mockKey)
    verify(mockStorageProvider).writeKey(any())
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(mockCipherProvider)
    verifyNoMoreInteractions(mockKeyStoreManager)
    verifyNoMoreInteractions(mockStorageProvider)
  }

  private fun verifyInteractionsForSave(
    testKey: String,
    testValue: String,
    encryptedValue: String
  ) {
    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).getAsymmetricKey(keyAlias)
    verify(mockCipherProvider).encrypt(eq(testValue.toByteArray()), eq(mockKey))
    verify(mockStorageProvider).writeItemToStorage(KCryptStorageItemEntity().apply {
      key = testKey
      value = encryptedValue
    })
  }

  private fun setupMocksForGet(testKey: String, encryptedValue: String, decryptedValue: String) {
    whenever(mockStorageProvider.getItemFromStorage(eq(testKey))).thenReturn(encryptedValue)
    whenever(mockCipherProvider.decrypt(any(), eq(encryptedValue))).thenReturn(
      stringToHex(
        decryptedValue
      ).toByteArray()
    )
    whenever(mockKeyStoreManager.getAsymmetricKey(keyAlias)).thenReturn(mockKey)
  }

  private fun verifyInteractionsForGet(testKey: String, encryptedValue: String) {
    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).getAsymmetricKey(keyAlias)
    verify(mockStorageProvider).getItemFromStorage(eq(testKey))
    verify(mockCipherProvider).decrypt(any(), eq(encryptedValue))
  }


  private fun stringToHex(input: String): String {
    val stringBuilder = StringBuilder()
    for (char in input) {
      val hexString = Integer.toHexString(char.toInt())
      stringBuilder.append(hexString)
    }
    return stringBuilder.toString()
  }

  fun byteArrayToHexString(byteArray: ByteArray): String {
    return byteArray.joinToString("") { "%02x".format(it) }
  }

}
