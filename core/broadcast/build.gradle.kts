plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.songlib.core.broadcast"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.serialization.json)

    // Pure-Kotlin (CIO) embedded server engine — no native/Netty bits, so it
    // runs fine inside an Android process.
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.websockets)
}
