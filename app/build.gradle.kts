plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.project.petpoint"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.project.petpoint"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packagingOptions {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    // ────────────────────────────────────────────────
    // Core Android & Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ────────────────────────────────────────────────
    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // LiveData support for Compose (you had a specific version → kept it)
    implementation("androidx.compose.runtime:runtime-livedata:1.7.0") // ← use stable version

    // ────────────────────────────────────────────────
    // Firebase (BOM + KTX versions only – no duplicates)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Remove: implementation(libs.firebase.auth)        ← non-KTX version – not needed
    // Remove: implementation(libs.firebase.database)   ← non-KTX version – not needed

    implementation("com.google.firebase:firebase-auth-ktx")     // ← Kotlin extensions → fixes getIdTokenResult()
    implementation("com.google.firebase:firebase-database-ktx") // ← Kotlin extensions for Realtime DB

    // ────────────────────────────────────────────────
    // Coroutines (needed for suspend functions + .await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1") // ← enables .await() on Firebase Tasks

    // ────────────────────────────────────────────────
    // Image & Media
    implementation("io.coil-kt:coil-compose:2.7.0")              // ← updated to latest stable
    implementation("com.cloudinary:cloudinary-android:2.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // ────────────────────────────────────────────────
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}