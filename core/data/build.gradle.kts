import java.util.Properties

plugins {
    alias(libs.plugins.songlib.android.library)
    alias(libs.plugins.songlib.hilt)
}

val localProperties = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) {
    localProperties.load(localFile.inputStream())
}

android {
    namespace = "com.songlib.core.data"

    defaultConfig {
        buildConfigField("String", "PaystackSecretKey", "\"${localProperties.getProperty("PAYSTACK_SECRET_KEY") ?: ""}\"")
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":core:network"))

    api(libs.androidx.compose.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
