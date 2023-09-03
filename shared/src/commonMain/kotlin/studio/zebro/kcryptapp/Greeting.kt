package studio.zebro.kcryptapp

import studio.zebro.kcrypt.getKCrypt

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        val key = getKCrypt().getEncryptionKeyToHexString(64)
        return "Hello, ${platform.name}! $key"
    }
}