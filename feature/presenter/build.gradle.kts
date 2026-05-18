plugins {
    alias(libs.plugins.songlib.android.feature)
    alias(libs.plugins.songlib.android.library.compose)
}

android {
    namespace = "com.songlib.feature.presenter"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
