plugins {
    alias(libs.plugins.songlib.android.feature)
    alias(libs.plugins.songlib.android.library.compose)
    alias(libs.plugins.songlib.hilt)
}

android {
    namespace = "com.songlib.feature.home"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.work.runtime)
    implementation(libs.coil.compose)
}
