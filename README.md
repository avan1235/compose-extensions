# Compose Extensions

This repository aims to bring some useful extensions for compose-multiplatform projects, which
are self-contained and allow to deploy compose applications to Android, iOS and Desktop platforms.

## How to use?

To make the extension available, place 

```kotlin
import("in.procyk.compose:extension-name:0.0.1")
```

into your `commonMain` source set's dependencies block, where `extension-name` should be replaced with the
proper name from the list of available extensions.

## Available Extensions

- `camera-qr` - detect QR codes camera view
