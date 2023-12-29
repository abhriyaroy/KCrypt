<p align="center"><img src="KCrypt.png" height="400"></p>

# KCrypt: Secure Key Management for Multiplatform Apps

KCrypt is a Kotlin Multiplatform Mobile (KMM) library that provides a secure and unified way to manage encryption keys across iOS and Android platforms. It abstracts the complexities of key generation, encryption, and storage, thereby providing you with a unique key that can be used for cryptographic purposes, allowing you to focus on building secure and robust multiplatform applications.

Kcrypt also provides a secured storage where in key-value pairs.

# Index
- [Features](#features)
- [Upcoming features](#upcoming-features)
- [Use case](#use-case)
- [Usage](#usage)
- [Compatibility](#compatibility)
- [Contributing](#contributing)
- [Test Reports](#test-reports)
- [License](#license)

## Features

- Secure Key Generation: KCrypt handles the generation of secure encryption keys based on platform-specific best practices and provides you with a unique key that persists as long as the app stays installed.
- Cross-Platform Compatibility: Build multiplatform applications with shared encryption logic that works consistently on iOS and Android.
- Storage of encrypted key-value pairs

## Upcoming features
- Ability to maintain multiple keys.

## Use case

If you want a secure String that you can use for encryption or any other cryptographic reasons, KCrpyt provides you with one.
For example:
If we take the [Realm Kmm library](https://github.com/realm/realm-kotlin), in order to encrypt the db we need to provide it with a key

```kotlin
val config = RealmConfiguration
      .Builder(
        schema = setOf(
          UserDbEntity::class,
          ClipboardDbEntity::class
        )
      )
      .schemaVersion(dbVersion)
      .encryptionKey(getKCrypt().getEncryptionKey()!!)
      .build()
```
We need to provide a key to the `encryptionKey()` method. Here comes KCrypt which provides a unique secure key across Android and iOS, that can be passed to the `encryptionKey()` method. Similarly, KCrypt can be used for other encryption/cryptographic purposes where one needs an encryption key.

Or

If you want to store data using key value pairs in an encrypted format, KCrypt is here for you.
For example:
If we want to store a value how we would normally store in encrypted shared preferences or keychain, we can do so as follows:

```kotlin

getKCrypt().saveString("stringVal", "my sample string")
val storedString = getKCrypt().getString("stringVal") // returns "my sample string"

```

## Usage

To get started with KCrypt in your KMM project, follow these steps:

1. Add the KCrypt dependency to your shared module's `build.gradle.kts`:
   
```kotlin
// build.gradle.kts in the shared module
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.abhriyaroy:KCrypt:0.0.15")
            }
        }
    }
}
```

2. Use KCrypt in your shared code:

- For Encryption key:

```kotlin
// Shared code
import studio.zebro.kcrypt.getKCrypt

val kCrypt = getKCrypt()

// to get encryption key in 64 byte array
val encryptionKeyByteArray : ByteArray = kCrypt.getEncryptionKey()

// to save a custom encryption key
kCrypt.saveEncryptionKey("MyCustomKey", isHexString = false)

// to get encryption key as a String representation of 64 byte array
val encryptionkeyInString : String = kCrypt.getEncryptionKeyToHexString()

// to convert byte array to string
val convertedByteArrayInString : String = kCrypt.byteArrayToHexString(encryptionKeyByteArray)

// to convert hex string to byte array
val convertedStringInByteArray : ByteArray = kCrypt.hexStringToByteArray(encryptionkeyInString)

```
  - For Key-Value pairs:

```kotlin
// Shared code
import studio.zebro.kcrypt.getKCrypt

val kCrypt = getKCrypt()

// save string value
kCrypt.saveString("stringVal", "my string")
// retrieve saved string value
kCrypt.getString("stringVal")

// save int value
kCrypt.saveInt("intVal", 101)
// retrieve saved int value
kCrypt.getInt("intVal")

// save float value
kCrypt.saveFloat("floatVal", 100.5f)
// retrieve float value
kCrypt.getFloat("floatVal")

// save double value
kCrypt.saveDouble("doubleVal", 599.4)
// retrieve double value
kCrypt.getDouble("doubleVal")

// save boolean value
kCrypt.saveBoolean("booleanVal", true)
// retrieve boolean value
kCrypt.getBoolean("booleanVal")

```


## Compatibility

KCrypt is designed to work with Kotlin Multiplatform Mobile projects targeting both iOS and Android platforms.

- Minimum Android version: Android 8.0 (API level 26)
- Minimum iOS version: iOS 13.0

## Contributing

Contributions to KCrypt are welcome! Feel free to open issues for feature requests, bug reports, or general discussions. Pull requests are also appreciated.

## Test Reports

![KCrypt Build and Test](https://github.com/abhriyaroy/KCrypt/actions/workflows/main.yaml/badge.svg)

You can view the latest test reports [here](https://github.com/abhriyaroy/KCrypt/actions/workflows/main.yaml).


## License

This project is licensed under the [MIT License](LICENSE).
