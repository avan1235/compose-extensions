rootProject.name = "examples"

// TODO: remove once compose-extensions built with the current versions is published
includeBuild("..")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":androidApp")
include(":desktopApp")
include(":shared")
include(":webApp")

