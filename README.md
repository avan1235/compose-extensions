[![Platforms](https://img.shields.io/badge/mobile-Android%20%7C%20iOS-blue)](https://github.com/avan1235/compose-extensions/releases)
[![Platforms](https://img.shields.io/badge/desktop-Windows%20%7C%20macOS%20%7C%20Linux-blue)](https://github.com/avan1235/compose-extensions/releases)

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.10-green.svg?logo=kotlin)](http://kotlinlang.org)
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
implementation("in.procyk.compose:extension-name:0.0.2")
```

into your `commonMain` source set's dependencies block, where `extension-name` should be replaced with the
proper name from the list of available extensions.

## Available Extensions

- `camera-qr` - detect QR codes camera view
- `camera-permission` - ask for camera permission
- `util` - handy functions to work with compose
