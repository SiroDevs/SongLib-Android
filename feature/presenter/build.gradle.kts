plugins {
    alias(libs.plugins.songlib.android.feature)
    alias(libs.plugins.songlib.android.library.compose)
}

android {
    namespace = "com.songlib.feature.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)
}
