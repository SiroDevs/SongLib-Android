import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.songlib.hilt)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.io.sentry)
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "com.songlib"
        versionCode = 852
        versionName = "1.0.852"
        minSdk = 26
        targetSdk = 37

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PaystackSecretKey", "\"${localProperties.getProperty("PAYSTACK_SECRET_KEY") ?: ""}\"")
        buildConfigField("String", "GoogleWebClientId", "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "SonglibApiKey", "\"${localProperties.getProperty("SONGLIB_API_KEY") ?: ""}\"")
    }

    signingConfigs {
        create("release") {
            keyAlias     = keystoreProperties["keyAlias"] as String
            keyPassword  = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
            storeFile    = keystoreProperties["storeFile"]?.let { file(it as String) }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix  = ".dev"
            versionNameSuffix    = "-dev"
            isDebuggable         = true
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig   = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }

    namespace = "com.songlib"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:selection"))
    implementation(project(":feature:home"))
    implementation(project(":feature:history"))
    implementation(project(":feature:drafts"))
    implementation(project(":feature:edits"))
    implementation(project(":feature:listing"))
    implementation(project(":feature:song"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:help"))
    implementation(project(":feature:howitworks"))
    implementation(project(":feature:donation"))

    // Navigation
    implementation(libs.compose.navigation)
    implementation(libs.compose.hilt.navigation)

    // Activity
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Monitoring
    implementation(libs.android.billing)
    implementation(libs.androidx.concurrent.futures)

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Google Sign-In
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.identity.googleid)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}

sentry {
    debug.set(true)
    includeSourceContext.set(true)
    org.set("futuristicken")
    projectName.set("songlib-android")
    authToken.set(localProperties.getProperty("SENTRY_AUTH_TOKEN"))
}
