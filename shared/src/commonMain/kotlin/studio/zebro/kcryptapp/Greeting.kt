package studio.zebro.kcryptapp

import studio.zebro.kcrypt.getKCrypt


class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}! ${getKCrypt()}"
    }
}