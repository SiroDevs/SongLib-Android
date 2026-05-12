plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
    kotlin("plugin.serialization") version "2.1.21"
}

android {
    namespace = "com.songlib.core.network"
    buildFeatures { buildConfig = true }
}

dependencies {
    api(project(":core:database"))

    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)
}
