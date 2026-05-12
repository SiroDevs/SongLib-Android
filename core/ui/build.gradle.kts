plugins {
    alias(libs.plugins.songlib.android.library.compose)
}

android {
    namespace = "com.songlib.core.ui"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:designsystem"))

    implementation(libs.lottie.compose)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.ui.tooling.preview)
}
