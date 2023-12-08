package studio.zebro.kcrypt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import platform.posix.arc4random_buf

interface IosPlatformHelper {
  fun generateRandomByteArray(length: Int): ByteArray
}

@OptIn(ExperimentalForeignApi::class)
class IosPlatformHelperImpl : IosPlatformHelper {
  override fun generateRandomByteArray(length: Int): ByteArray {
    val byteArray = ByteArray(length)
    arc4random_buf(byteArray.refTo(0), byteArray.size.convert())
    return byteArray
  }
}