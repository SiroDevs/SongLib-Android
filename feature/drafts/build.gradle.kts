plugins {
    alias(libs.plugins.songlib.android.feature)
    alias(libs.plugins.songlib.android.library.compose)
}

android {
    namespace = "com.songlib.feature.drafts"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:casting"))
    implementation(project(":feature:song"))
    implementation(libs.androidx.foundation)
}
