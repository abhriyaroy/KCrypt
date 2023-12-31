pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "KCrypt"
include(":androidApp")
include(":shared")
include(":KCrypt")
includeBuild("convention-plugins")
