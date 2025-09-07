pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val shadowVersion: String by settings
        kotlin("jvm") version kotlinVersion
        id("com.gradleup.shadow") version shadowVersion
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "apc"

include("cli", "core")
