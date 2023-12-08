import io.realm.kotlin.types.RealmObject

class KCryptEntity : RealmObject {
  var encodedKey: String = ""
  var isStringInHex : Boolean = false

  override fun toString(): String {
    return "key:$encodedKey isStringInHex:$isStringInHex"
  }

  override fun equals(other: Any?): Boolean {
    return if (other !is KCryptEntity) {
      false
    } else {
      other.encodedKey == this.encodedKey
    }
  }
}
