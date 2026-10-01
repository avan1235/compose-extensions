import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply true
    alias(libs.plugins.android.kotlin.multiplatform.library) apply true
    alias(libs.plugins.compose.multiplatform) apply true
    alias(libs.plugins.compose.compiler) apply true
}

kotlin {
    jvmToolchain(17)

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    android {
        namespace = "in.procyk.compose.examples.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.animation.graphics)

            implementation(libs.compose.extensions.calendar)
            implementation(libs.compose.extensions.camera.permission)
            implementation(libs.compose.extensions.camera.qr)
            implementation(libs.compose.extensions.util)
        }
    }
}
