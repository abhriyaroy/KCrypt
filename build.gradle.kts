plugins {
  //trick: for the same plugin versions in all sub-modules
  id("com.android.application").version("7.4.1").apply(false)
  id("com.android.library").version("7.4.1").apply(false)
  kotlin("android").version("1.8.10").apply(false)
  kotlin("multiplatform").version("1.8.10").apply(false)
  id("io.github.gradle-nexus.publish-plugin").version("1.1.0").apply(false)
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
