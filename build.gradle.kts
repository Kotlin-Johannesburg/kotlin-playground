plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
}

group = "jhb.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.4.21")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")


    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.2.0")
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", "5.2.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}