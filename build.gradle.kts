// UBICACIÓN: PDA/build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("org.jetbrains.dokka") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.26" apply false
}