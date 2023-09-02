<p align="center"><img src="KCrypt.png" height="400"></p>

# KCrypt: Secure Key Management for Multiplatform Apps

KCrypt is a Kotlin Multiplatform Mobile (KMM) library that provides a secure and unified way to manage encryption keys across iOS and Android platforms. It abstracts the complexities of key generation, encryption, and storage, thereby providing you with a unique key that can be used for cryptographic purposes, allowing you to focus on building secure and robust multiplatform applications.

## Features

- Secure Key Generation: KCrypt handles the generation of secure encryption keys based on platform-specific best practices and provides you with a unique key that persists as long as the app stays installed.
- Cross-Platform Compatibility: Build multiplatform applications with shared encryption logic that works consistently on iOS and Android.

## Upcoming features
- Storage of key-value pairs securely.
- Ability to maintain multiple keys.

## Usage

To get started with KCrypt in your KMM project, follow these steps:

1. Add the KCrypt dependency to your shared module's `build.gradle.kts`:
   
```kotlin
// build.gradle.kts in the shared module
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.abhriyaroy:KCrypt:${latestVersion}")
            }
        }
    }
}
```
The latest version of the library is ![Latest Version](https://maven-badges.herokuapp.com/maven-central/io.github.abhriyaroy/KCrypt/badge.svg)

2. Use KCrypt in your shared code:

```kotlin
// Shared code
import your.group.id.getKCrypt

val kCrypt = getKCrypt()
val encryptionKey = kCrypt.getEncryptionKey()
```


## Compatibility

KCrypt is designed to work with Kotlin Multiplatform Mobile projects targeting both iOS and Android platforms.

- Minimum Android version: Android 8.0 (API level 26)
- Minimum iOS version: iOS 13.0

## Contributing

Contributions to KCrypt are welcome! Feel free to open issues for feature requests, bug reports, or general discussions. Pull requests are also appreciated.

## License

This project is licensed under the [MIT License](LICENSE).
