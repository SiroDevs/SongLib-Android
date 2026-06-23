plugins {
    alias(libs.plugins.songlib.android.feature)
}

android {
    namespace = "com.songlib.feature.settings"
}

dependencies {
    implementation(project(":core:data"))

    // Profile photo loading
    implementation(libs.coil.compose)
}
