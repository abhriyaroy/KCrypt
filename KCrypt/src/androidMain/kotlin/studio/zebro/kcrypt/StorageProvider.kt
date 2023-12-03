package studio.zebro.kcrypt

import KCryptEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.migration.AutomaticSchemaMigration
import studio.zebro.kcrypt.entity.KCryptStorageItemEntity

interface StorageProvider {

  fun writeKey(kCryptEntity: KCryptEntity)
  fun getKey() : KCryptEntity

  fun writeItemToStorage(kCryptStorageItemEntity: KCryptStorageItemEntity)
  fun getItemFromStorage(key : String) : String
}

class StorageProviderImpl : StorageProvider {

  private val realmName = "krypt.realm"
  private var realm: Realm? = null

  override fun writeKey(kCryptEntity: KCryptEntity) {
    getRealm().writeBlocking {
      delete(KCryptEntity::class)
      copyToRealm(kCryptEntity)
    }
  }

  override fun getKey() : KCryptEntity {
    return getRealm().query(KCryptEntity::class).find().first()

  }

  override fun writeItemToStorage(kCryptStorageItemEntity: KCryptStorageItemEntity) {
    getRealm().writeBlocking {
      copyToRealm(kCryptStorageItemEntity, updatePolicy = UpdatePolicy.ALL)
    }
  }

  override fun getItemFromStorage(key: String) : String {
    return getRealm().query(KCryptStorageItemEntity::class).find().filter { it.key == key }.let {
      it.first().value
    }
  }

  private fun getRealm(): Realm {
    if (realm == null) {

      val configuration = RealmConfiguration
        .Builder(
          schema = setOf(
            KCryptEntity::class,
            KCryptStorageItemEntity::class
          )
        )
        .schemaVersion(2)
        .migration(object : AutomaticSchemaMigration {
          override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {

          }
        })
        .name(realmName)
        .build()
      realm = Realm.open(configuration)
    }
    return realm!!
  }

}