import java.io.FileInputStream
import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.weathersync"
    compileSdk = 35

    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(file))

    defaultConfig {
        applicationId = "com.example.weathersync"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "WEATHER_API_KEY", properties.getProperty("WEATHER_API_KEY"))
        buildConfigField("String", "PLACES_API_KEY", properties.getProperty("PLACES_API_KEY"))
        resValue ("string", "maps_api_key", properties.getProperty("PLACES_API_KEY"))


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
        buildConfig = true
    }
}

dependencies {
    //lottie
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    //navigation
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha06")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    //location
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    //glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    //room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //livedata & viewmodel
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")

    //pull refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0")

    //google maps
    implementation("com.google.maps.android:maps-compose:4.1.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:places-compose:0.1.3")
    implementation("com.google.android.libraries.places:places:3.4.0")

    // worker-manager
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    // testing
    val junitVersion = "4.13.2"
    val hamcrestVersion = "1.3"
    val archTestingVersion = "2.2.0"
    val robolectricVersion = "4.5.1"
    val androidXTestCoreVersion = "1.6.1"
    val androidXTestExtKotlinRunnerVersion = "1.1.5"
    val espressoVersion = "3.5.1"
    val coroutinesVersion = "1.5.2"
    // Dependencies for local unit tests
    testImplementation ("junit:junit:$junitVersion")
    testImplementation ("org.hamcrest:hamcrest-all:$hamcrestVersion")
    testImplementation ("androidx.arch.core:core-testing:$archTestingVersion")
    testImplementation ("org.robolectric:robolectric:$robolectricVersion")

    // AndroidX Test - JVM testing
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    //testImplementation "androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion"

    // AndroidX Test - Instrumented testing
    androidTestImplementation ("androidx.test:core:$androidXTestExtKotlinRunnerVersion")
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")

    //Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")

    // hamcrest
    testImplementation ("org.hamcrest:hamcrest:2.2")
    testImplementation ("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")


    // AndroidX and Robolectric
    testImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation ("org.robolectric:robolectric:4.11")

    // InstantTaskExecutorRule
    testImplementation ("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")

    //kotlinx-coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("io.mockk:mockk-android:1.13.17")
    testImplementation("io.mockk:mockk-agent:1.13.17")
    androidTestImplementation ("androidx.arch.core:core-testing:$archTestingVersion")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}