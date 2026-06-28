plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.songlib.core.casting"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.websockets)
}
