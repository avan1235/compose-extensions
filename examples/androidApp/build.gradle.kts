plugins {
    alias(libs.plugins.android.application) apply true
    alias(libs.plugins.compose.compiler) apply true
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "in.procyk.compose.examples"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        applicationId = "in.procyk.compose.examples.Application"
        versionCode = 1
        versionName = "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.androidx.core.ktx)
}
