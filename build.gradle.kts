import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("convention.publication") apply false

    alias(libs.plugins.gradle.versions) apply true
    alias(libs.plugins.version.catalog.update) apply true
}

subprojects {
    // library modules have no wasmJs UI tests, so the Compose check requiring an executable wasmJs binary is not applicable
    tasks.matching { it.name.startsWith("checkComposeUiTestConfigurationFor") }.configureEach {
        enabled = false
    }
}

versionCatalogUpdate {
    sortByKey = true
}

fun isStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return stableKeyword || regex.matches(version)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        !isStable(candidate.version)
    }
}
