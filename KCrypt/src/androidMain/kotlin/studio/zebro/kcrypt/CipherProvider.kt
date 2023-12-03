package studio.zebro.kcrypt

import java.security.Key
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

interface CipherProvider {
  fun encrypt(input : ByteArray, key : Key) : String
  fun decrypt(key : Key, data : String) : ByteArray
}

class CipherProviderImpl : CipherProvider {

  override fun encrypt(input: ByteArray, key: Key): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val (cipherText, iv) = cipher.doFinal(input) to cipher.iv
    return (Base64.getEncoder()
      .encodeToString(cipherText) +
        "," +
        Base64.getEncoder().encodeToString(iv)).apply {
      println("the encoded param is $this")
    }
  }

  override fun decrypt(key: Key, data: String): ByteArray {
    val parts = data.split(",")
    val plainTextByteArray = Base64.getDecoder().decode(parts[0])
    val iv = Base64.getDecoder().decode(parts[1])
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    return cipher.doFinal(plainTextByteArray)
  }
}