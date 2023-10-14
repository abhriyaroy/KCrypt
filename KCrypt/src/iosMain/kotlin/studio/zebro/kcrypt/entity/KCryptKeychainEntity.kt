package studio.zebro.kcrypt.entity

import kotlinx.serialization.Serializable
@Serializable
data class KCryptKeychainEntity(val key: String, val isStringInHex: Boolean)