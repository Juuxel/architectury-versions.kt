import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "io.github.juuxel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation("io.ktor:ktor-client-java:2.0.3")
    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("info.picocli:picocli:4.6.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Version" to project.version,
            "Main-Class" to "juuxel.toolchainversions.MainKt",
        )
    }
}

application {
    mainClass.set("juuxel.toolchainversions.MainKt")
}
