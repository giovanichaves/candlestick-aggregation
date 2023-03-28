plugins {
  kotlin("jvm") version "1.7.21"
  application
}

application {
  mainClass.set("MainKt")
}

group = "org.traderepublic.candlesticks"
version = "1.1.4"

repositories {
  mavenCentral()
}

object DependencyVersions {
  const val coroutines = "1.6.4"
  const val http4k = "4.34.0.3"
  const val jackson = "2.14.0"
  const val mockk = "1.13.2"
  const val logback = "1.2.11"
  const val restassured = "5.3.0"
  const val kotest = "5.5.5"
  const val junit = "5.9.2"
}

dependencies {
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))


  implementation(platform("org.http4k:http4k-bom:${DependencyVersions.http4k}"))
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-server-netty")
  implementation("org.http4k:http4k-client-websocket")
  implementation("org.http4k:http4k-format-jackson")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.coroutines}")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersions.jackson}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${DependencyVersions.jackson}")
  implementation("ch.qos.logback:logback-classic:${DependencyVersions.logback}")
  testImplementation("io.mockk:mockk:${DependencyVersions.mockk}")
  testImplementation("org.junit.jupiter:junit-jupiter-params:${DependencyVersions.junit}")
  testImplementation("io.rest-assured:kotlin-extensions:${DependencyVersions.restassured}")
  testImplementation("io.kotest:kotest-runner-junit5:${DependencyVersions.kotest}")
}

tasks.test {
  useJUnitPlatform()
}
