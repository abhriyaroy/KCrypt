package studio.zebro.kcryptapp

import studio.zebro.kcrypt.getKCrypt

class Greeting {
  private val platform: Platform = getPlatform()

  fun greet(): String {
//        getKCrypt().saveEncryptionKey("sadasd", false)
    val key = hexToString(getKCrypt().getEncryptionKeyToHexString())
    val string = "abcaa11"
    val intValue = 11080
    val floatValue = 1100.18f
    val doubleValue = 11088.67
    val booleanValue = true
    getKCrypt().saveString("stringVal", string)
    getKCrypt().saveInt("intVal", intValue)
    getKCrypt().saveFloat("floatVal", floatValue)
    getKCrypt().saveDouble("doubleVal", doubleValue)
    getKCrypt().saveBoolean("booleanVal", booleanValue)
    return "Hello, ${platform.name}! $key \n fetched values = ${getKCrypt().getString("stringVal")} , ${getKCrypt().getInt("intVal")} , " +
        "${getKCrypt().getFloat("floatVal")} , ${getKCrypt().getDouble("doubleVal")}, ${getKCrypt().getBoolean("booleanVal1")}"
  }

  fun hexToString(hex: String): String {
    val result = StringBuilder()
    for (i in 0 until hex.length step 2) {
      val byte = hex.substring(i, i + 2).toInt(16).toByte()
      result.append(byte.toChar())
    }
    return result.toString()
  }
}