plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

layout.buildDirectory.set(rootProject.layout.buildDirectory.dir("mate"))
android {
    namespace = "app.yai.shells.mate"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
}

dependencies {
    api(project(":vizo"))
    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
}
