plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Phone and TV can build against the same checkout at the same time.
layout.buildDirectory.set(rootProject.layout.buildDirectory.dir("vizo"))

android {
    namespace = "app.yai.shells.vizo"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    api("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.13.0")
}
