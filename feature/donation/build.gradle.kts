plugins {
    alias(libs.plugins.songlib.android.feature)
}

android {
    namespace = "com.songlib.feature.donation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
