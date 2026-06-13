plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
}

android {
    namespace = "com.songlib.core.data"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:network"))

    api(libs.androidx.compose.material)

    implementation(platform(libs.jan.tennert.supabase.bom))
    implementation(libs.jan.tennert.supabase.postgrest)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)
}
