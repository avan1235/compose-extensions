[![Platforms](https://img.shields.io/badge/mobile-Android%20%7C%20iOS-blue)](https://github.com/avan1235/compose-extensions/releases)
[![Platforms](https://img.shields.io/badge/desktop-Windows%20%7C%20macOS%20%7C%20Linux-blue)](https://github.com/avan1235/compose-extensions/releases)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-green.svg?logo=kotlin)](https://github.com/JetBrains/kotlin)
[![Kotlin](https://img.shields.io/badge/Compose%20Multiplatform-1.5.10-green.svg?logo=jetpack-compose)](https://github.com/JetBrains/compose-multiplatform)
[![Maven Central](https://img.shields.io/maven-central/v/in.procyk.compose/camera-qr?label=Maven%20Central&color=green)](https://central.sonatype.com/namespace/in.procyk.compose)
[![Latest Release](https://img.shields.io/github/v/release/avan1235/compose-extensions?label=Release&color=green)](https://github.com/avan1235/compose-extensions/releases)
[![Build](https://img.shields.io/github/actions/workflow/status/avan1235/compose-extensions/release.yml?label=Build&color=green)](https://github.com/avan1235/compose-extensions/actions/workflows/release.yml)

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](./LICENSE.md)
[![GitHub Repo stars](https://img.shields.io/github/stars/avan1235/compose-extensions?style=social)](https://github.com/avan1235/compose-extensions/stargazers)
[![Fork OpenOTP](https://img.shields.io/github/forks/avan1235/compose-extensions?logo=github&style=social)](https://github.com/avan1235/compose-extensions/fork)

# Compose Multiplatform Extensions

This repository aims to bring some useful extensions for compose-multiplatform projects, which
are self-contained and allow to deploy compose applications to Android, iOS and Desktop platforms.

## How to use?

To make the extension available, place 

```kotlin
implementation("in.procyk.compose:extension-name:1.5.10.0")
```

into your `commonMain` source set's dependencies block, where `extension-name` should be replaced with the
proper name from the list of available extensions.

## Available Extensions

- `camera-qr` - detect QR codes camera view
    ```kotlin
    implementation("in.procyk.compose:camera-qr:1.5.10.0")
    ```
    - for Android Application add to [AndroidManifest.xml](./examples/src/androidMain/AndroidManifest.xml)
      ```xml
      <uses-permission android:name="android.permission.CAMERA"/>
      <uses-feature
          android:name="android.hardware.camera"
          android:required="false"/>```

    - for iOS Application add `Privacy - Camera Usage Description` entry to [Info.plist](./examples/xcode/iosApp/Info.plist)
      ```xml
      <key>NSCameraUsageDescription</key>
      <string></string>
      ```
   
    - for Desktop Application add to [build.gradle.kts](./examples/build.gradle.kts)
      ```kotlin
      runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
      infoPlist {
          extraKeysRawXml = """
              <key>NSCameraUsageDescription</key>
              <string></string>
          """.trimIndent()
      }
      ```
      to your `macOS { ... }` block  and include 
      [runtime-entitlements.plist](./examples/runtime-entitlements.plist) in your project source files

- `camera-permission` - ask for camera permission
    ```kotlin
    implementation("in.procyk.compose:camera-permission:1.5.10.0")
    ```

- `util` - handy functions to work with compose
    ```kotlin
    implementation("in.procyk.compose:util:1.5.10.0")
    ```
