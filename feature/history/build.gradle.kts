plugins {
    alias(libs.plugins.songlib.android.feature)
    alias(libs.plugins.songlib.android.library.compose)
}

android {
    namespace = "com.songlib.feature.history"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(libs.androidx.foundation)
}
