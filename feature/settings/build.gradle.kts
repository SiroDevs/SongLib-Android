plugins {
    alias(libs.plugins.songlib.android.feature)
}

android {
    namespace = "com.songlib.feature.settings"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
