// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.gms.google-services") version "4.4.1" apply false // Or your chosen version
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}