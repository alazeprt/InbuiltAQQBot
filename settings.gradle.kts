rootProject.name = "InbuiltAQQBot"
include("common")
include("bukkit")
include("folia")
include("bungeecord")
include("velocity")
include("sponge")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.spongepowered.org/maven")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}