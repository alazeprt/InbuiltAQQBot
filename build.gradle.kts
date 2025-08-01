import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    mavenCentral()
}

dependencies {}

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

tasks.build {
    dependsOn("shadowJar")
}

subprojects {
    tasks.withType<Jar> {
        manifest {
            attributes("Implementation-Version" to rootProject.version)
            attributes("Implementation-Vendor" to "alazeprt")
            attributes("Implementation-Website" to "https://aqqbot.alazeprt.top/")
        }
    }
}