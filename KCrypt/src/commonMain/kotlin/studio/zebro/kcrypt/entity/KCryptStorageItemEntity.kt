package studio.zebro.kcrypt.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class KCryptStorageItemEntity : RealmObject {
  @PrimaryKey
  var key: String = ""
  var value: String = ""

  override fun toString(): String {
    return "key:$key value:$value"
  }

  override fun equals(other: Any?): Boolean {
    return if (other !is KCryptStorageItemEntity) {
      false
    } else {
      other.key == this.key
    }
  }
}