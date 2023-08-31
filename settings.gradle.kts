pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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
