plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("io.realm.kotlin") version "1.10.0"
  id("convention.publication")
  kotlin("plugin.serialization") version "1.9.0"
}

kotlin {
  android {
    compilations.all {
      kotlinOptions {
        jvmTarget = "1.8"
      }
    }
    publishLibraryVariants("debug", "release")
  }

  ios {
    binaries.framework()
  }

  iosSimulatorArm64 {
    binaries.framework()
  }

  sourceSets {

    val commonMain by getting {
      dependencies {
        implementation("io.realm.kotlin:library-base:1.10.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val androidMain by getting
    val androidUnitTest by getting {
      dependencies {
//        implementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
//        implementation("org.mockito:mockito-inline:5.1.0")

        implementation("junit:junit:4.13.2")
        implementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.10")

        implementation("org.mockito:mockito-core:4.0.0")
        implementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

      }
    }
    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by getting {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
    }
    val iosX64Test by getting
    val iosArm64Test by getting
    val iosSimulatorArm64Test by getting
    val iosTest by getting {
      dependsOn(commonTest)
      iosX64Test.dependsOn(this)
      iosArm64Test.dependsOn(this)
      iosSimulatorArm64Test.dependsOn(this)
    }
  }
}

android {
  namespace = "studio.zebro.kcrypt"
  compileSdk = 34
  defaultConfig {
    minSdk = 26
  }
}