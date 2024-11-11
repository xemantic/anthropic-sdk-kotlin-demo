import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.plugin.serialization)
  application
}

val javaTarget = libs.versions.javaTarget.get()
val kotlinTarget = KotlinVersion.fromVersion(libs.versions.kotlinTarget.get())

kotlin {
  // set up according to https://jakewharton.com/gradle-toolchains-are-rarely-a-good-idea/
  compilerOptions {
    apiVersion = kotlinTarget
    languageVersion = kotlinTarget
    jvmTarget = JvmTarget.fromTarget(javaTarget)
    freeCompilerArgs.add("-Xjdk-release=$javaTarget")
    progressiveMode = true
  }
}

tasks.compileJava {
  options.release = javaTarget.toInt()
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.anthropic.sdk.kotlin)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.core)
  runtimeOnly(libs.ktor.client.java)
  runtimeOnly(libs.log4j.slf4j2)
  runtimeOnly(libs.log4j.core)
  runtimeOnly(libs.jackson.databind)
  runtimeOnly(libs.jackson.dataformat.yaml)
}
