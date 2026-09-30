plugins {
    alias(libs.plugins.android.application) apply true
}

android {
    namespace = "in.procyk.compose.examples.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        applicationId = "in.procyk.compose.examples.Application"
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // activity, manifest entries and resources are provided by the shared multiplatform module
    implementation(project(":"))
}
