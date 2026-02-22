plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.7" apply false
}
