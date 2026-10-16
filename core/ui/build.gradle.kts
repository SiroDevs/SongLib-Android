plugins {
    alias(libs.plugins.songlib.android.library.compose)
    alias(libs.plugins.songlib.hilt)
}

android {
    namespace = "com.songlib.core.ui"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:design_system"))

    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.hilt.android)
    implementation(libs.zxing.core)
    implementation(libs.androidx.icons.extended)
    implementation(libs.androidx.core.ktx)
}
