plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
}

android {
    namespace = "com.songlib.core.data"
}

dependencies {
    api(project(":core:database"))
    api(project(":core:network"))
    implementation(project(":core:common"))

    api(libs.androidx.compose.material)

    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.androidx.core.ktx)
    implementation(libs.revenuecat)
}
