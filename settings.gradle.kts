pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.library") version "9.4.0"
        id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "yai-player-shells"
include(":vizo")
