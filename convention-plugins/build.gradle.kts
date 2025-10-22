plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.34.0")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}