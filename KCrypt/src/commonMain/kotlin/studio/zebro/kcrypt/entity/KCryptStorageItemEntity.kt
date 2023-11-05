package studio.zebro.kcrypt.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class KCryptStorageItemEntity : RealmObject {
  @PrimaryKey var key : String = ""
  var value : String = ""
}