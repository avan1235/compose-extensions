import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply true
    alias(libs.plugins.android.kotlin.multiplatform.library) apply true
    alias(libs.plugins.compose.multiplatform) apply true
    alias(libs.plugins.compose.compiler) apply true

    id("convention.publication") apply true
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = "in.procyk.compose"
version = libs.versions.compose.extensions.get()

kotlin {
    jvm("desktop")

    jvmToolchain(17)

    android {
        namespace = "in.procyk.compose.camera.permission"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val stubMain by creating
        val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)

            implementation(project(":util"))
        }
        stubMain.dependsOn(commonMain.get())

        androidMain.dependencies {
            implementation(libs.accompanist.permissions)
        }
        desktopMain.dependencies {
            implementation(libs.webcam.capture)
            implementation(libs.webcam.capture.driver.native)
        }
        wasmJsMain.get().dependsOn(stubMain)
    }
}
