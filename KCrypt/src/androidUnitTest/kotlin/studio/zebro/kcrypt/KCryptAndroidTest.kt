package studio.zebro.kcrypt

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

    kCryptAndroid.saveString(testKey, testValue)

    verify(mockKeyStoreManager).initializeKeystore()
    verify(mockKeyStoreManager).getAsymmetricKey(keyAlias)
    verify(mockCipherProvider).encrypt(eq(stringToHex(testValue).toByteArray()), any())
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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)

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
    verify(mockCipherProvider).encrypt(eq(testValue.toByteArray()), any())
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
    whenever(mockKeyStoreManager.getAsymmetricKey(any())).thenReturn(mockKey)
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

}
