plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    namespace = "com.example.composefirsttry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.giftcardsapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.fragment)
    kapt(libs.hilt.compiler)

    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.crypto)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.extensions)
    implementation(libs.livedata)
    implementation(libs.livedata.core)

    implementation(libs.okhttp3)
    implementation(libs.gson)
    implementation(libs.constraintlayout)
    implementation(libs.preference)

    implementation(libs.glide)
    implementation(libs.glide.compiler)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.android.test)
}