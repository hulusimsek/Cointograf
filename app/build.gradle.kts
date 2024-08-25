plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("androidx.room") version "2.6.1" apply false
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.hulusimsek.cryptoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hulusimsek.cryptoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")


    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("com.google.accompanist:accompanist-flowlayout:0.17.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    // Timber
    implementation ("com.jakewharton.timber:timber:4.7.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // Coil
    implementation ("io.coil-kt:coil-compose:1.3.2")

    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.49")
    kapt ("com.google.dagger:hilt-android-compiler:2.49")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation ("androidx.hilt:hilt-work:1.2.0")
    implementation ("androidx.work:work-runtime-ktx:2.9.1")


    val nav_version = "2.7.7"

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.compose.foundation:foundation:1.6.8")
    implementation ("androidx.compose.ui:ui:1.6.8")


    implementation("com.google.accompanist:accompanist-pager:0.28.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")
    implementation ("com.github.skydoves:landscapist:1.4.5")
    implementation ("androidx.compose.material:material:1.1.1")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")

    implementation("androidx.core:core-splashscreen:1.2.0-alpha01")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.28")

    // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m2:2.0.0-alpha.28")

    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.28")

    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.28")

    // For the view system.
    implementation("com.patrykandpatrick.vico:views:2.0.0-alpha.28")



}