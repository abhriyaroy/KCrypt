package studio.zebro.kcryptapp

import studio.zebro.kcrypt.getKCrypt

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
//        getKCrypt().saveEncryptionKey("sadasd", false)
        val key = hexToString(getKCrypt().getEncryptionKeyToHexString())
        return "Hello, ${platform.name}! $key"
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