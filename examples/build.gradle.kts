import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "1.9.23"
    id("com.android.application") version "8.2.2"
    id("org.jetbrains.compose") version "1.6.1"
}

group = "in.procyk.compose"
version = "1.0.0"

kotlin {
    jvm("desktop")

    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    listOf(
        iosX64(),
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.animationGraphics)

            val composeExtensionsVersion = "1.6.1.0"
            implementation("in.procyk.compose:calendar:$composeExtensionsVersion")
            implementation("in.procyk.compose:camera-permission:$composeExtensionsVersion")
            implementation("in.procyk.compose:camera-qr:$composeExtensionsVersion")
            implementation("in.procyk.compose:util:$composeExtensionsVersion")
            implementation("in.procyk.compose:qr-code:$composeExtensionsVersion")
        }
        androidMain.dependencies {
            api("androidx.activity:activity-compose:1.8.2")
            api("androidx.appcompat:appcompat:1.6.1")
            api("androidx.core:core-ktx:1.12.0")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    compileSdk = 34
    namespace = "in.procyk.compose.examples"

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        applicationId = "in.procyk.compose.examples.Application"
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
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
