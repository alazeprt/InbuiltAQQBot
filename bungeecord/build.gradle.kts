import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.25"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "top.alazeprt.iab"
version = properties["version"] as String

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks.jar {
    archiveFileName.set("InbuiltAQQBot-${archiveFileName.get()}")
}

tasks.shadowJar {
    archiveFileName.set("InbuiltAQQBot-${archiveFileName.get()}")
    relocate("org.apache.commons.io", "top.alazeprt.iab.shaded.org.apache.commons.io")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}