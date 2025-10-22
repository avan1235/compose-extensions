import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply true
    alias(libs.plugins.android.library) apply true
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

    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
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
            implementation(compose.runtime)
            implementation(compose.foundation)

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

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    namespace = "in.procyk.compose.camera.permission"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
