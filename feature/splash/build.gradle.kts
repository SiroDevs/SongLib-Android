plugins {
    alias(libs.plugins.songlib.android.feature)
}

android {
    namespace = "com.songlib.feature.splash"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
