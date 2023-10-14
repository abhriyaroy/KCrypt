import io.realm.kotlin.types.RealmObject

class KCryptEntity : RealmObject {
  var encodedKey: String = ""
  var isStringInHex : Boolean = false
}
