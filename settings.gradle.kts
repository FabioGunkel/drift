pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "drift"
include(":app")
include(":affectus")

// Core modules
include(":core:network")
include(":core:ui")
include(":core:common")
include(":core:domain")
include(":core:user")

// Feature: Map
include(":feature:map:ui")
include(":feature:map:domain")
include(":feature:map:data")
