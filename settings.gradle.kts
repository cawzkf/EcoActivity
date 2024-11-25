pluginManagement {
    repositories {
        google() // Plugins Android
        mavenCentral() // Plugins genéricos e outras dependências
        gradlePluginPortal() // Opcional
    }
    plugins {
        id("com.android.application") version "8.7.2"
        id("org.jetbrains.kotlin.android") version "1.9.10"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ecoactivity"
include(":app")
