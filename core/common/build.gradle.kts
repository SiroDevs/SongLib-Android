plugins {
    alias(libs.plugins.songlib.android.library)
}

android {
    namespace = "com.songlib.core.common"
}

dependencies {
    api(project(":core:database"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.ktor.client.android)
    implementation(libs.revenuecat)
}
