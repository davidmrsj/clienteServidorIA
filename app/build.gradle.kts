plugins {
    // Aplicamos en este módulo los plugins que sí usaremos
    alias(libs.plugins.android.application) // <--- Se aplica el plugin de Android App
    alias(libs.plugins.kotlin.android)      // <--- Se aplica Kotlin Android
    alias(libs.plugins.hilt)               // <--- Se aplica Hilt sin "apply false"
    kotlin("kapt")                         // <--- Habilita KAPT (anotaciones)
}

android {
    namespace = "com.example.aplicacion1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aplicacion1"
        minSdk = 33
        targetSdk = 35
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
        viewBinding = true
    }
}

dependencies {
    // Core + AppCompat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Material Design
    implementation(libs.material)

    // Activity & ConstraintLayout
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle & LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Hilt - DAGGER
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.logging.interceptor)

    // Retrofit + Gson + OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    // ExoPlayer (Media3)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Glide
    implementation(libs.bumptech.glide.glide)
    kapt(libs.bumptech.glide.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
