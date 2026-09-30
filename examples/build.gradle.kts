import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply true
    alias(libs.plugins.android.kotlin.multiplatform.library) apply true
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.multiplatform) apply true
    alias(libs.plugins.compose.compiler) apply true
}

group = "in.procyk.compose"
version = "1.0.0"

kotlin {
    jvm("desktop")

    jvmToolchain(17)

    android {
        namespace = "in.procyk.compose.examples"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
    }

    applyDefaultHierarchyTemplate()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

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
        androidMain.dependencies {
            api(libs.androidx.activity.compose)
            api(libs.androidx.appcompat.appcompat)
            api(libs.androidx.core.ktx)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "in.procyk.compose.examples.MainKt"
        version = "1.0.0"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "Application"

            windows {
                menu = false
                upgradeUuid = "01397045-117e-43df-8801-9de544899aef"
            }

            macOS {
                bundleID = "in.procyk.compose.examples.Application"
                appStore = false
                signing {
                    sign = false
                }
                runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
                infoPlist {
                    extraKeysRawXml = """
                        <key>NSCameraUsageDescription</key>
                        <string></string>
                    """.trimIndent()
                }
            }
        }
    }
}
