package studio.zebro.kcrypt

interface KCrypt {
  fun getEncryptionKey(): String
}

expect fun getKCrypt(): KCrypt