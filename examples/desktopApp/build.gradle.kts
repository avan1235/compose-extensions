import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.compose.multiplatform) apply true
    alias(libs.plugins.compose.compiler) apply true
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.ui)
}

compose.desktop {
    application {
        mainClass = "in.procyk.compose.examples.MainKt"
        version = "1.0.0"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "Application"
            packageVersion = "1.0.0"

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
