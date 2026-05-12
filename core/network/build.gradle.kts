plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
}

android {
    namespace = "com.songlib.core.network"
    buildFeatures { buildConfig = true }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)
}
