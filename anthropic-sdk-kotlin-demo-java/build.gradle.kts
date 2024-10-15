plugins {
  java
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.anthropic.sdk.kotlin)
  implementation(libs.ktor.client.java)
  runtimeOnly(libs.log4j.slf4j2)
  runtimeOnly(libs.log4j.core)
  runtimeOnly(libs.jackson.databind)
  runtimeOnly(libs.jackson.dataformat.yaml)
}
