plugins {
  //trick: for the same plugin versions in all sub-modules
  id("com.android.application").version("8.1.0").apply(false)
  id("com.android.library").version("8.1.0").apply(false)
  kotlin("android").version("1.9.10").apply(false)
  kotlin("multiplatform").version("1.9.10").apply(false)
  id("io.github.gradle-nexus.publish-plugin").version("1.1.0").apply(false)
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
