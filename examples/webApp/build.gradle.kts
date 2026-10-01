import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply true
    alias(libs.plugins.compose.multiplatform) apply true
    alias(libs.plugins.compose.compiler) apply true
}

kotlin {
    // compose-extensions libraries are published for wasmJs only (no js target)
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            implementation(libs.compose.ui)
        }
    }
}
