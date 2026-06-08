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
    namespace = "com.songlib.core.network"

    defaultConfig {
        buildConfigField(
            "String", "SONGLIB_API_KEY",
            "\"${localProperties.getProperty("SONGLIB_API_KEY") ?: ""}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:database"))

    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)
}
