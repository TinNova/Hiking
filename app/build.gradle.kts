plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.tinnovakovic.hiking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tinnovakovic.hiking"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    val composeVersion = "1.5.0"
    val lifecycleVersion = "2.6.1"
    val retrofitVersion = "2.9.0"
    val jUnitVersion = "5.7.2"
    val mockkVersion = "1.12.2"
    val okHttp = "4.11.0"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")


    //Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-unit:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")

    //Jetpack Compose Material Design Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.7.1")

    implementation("io.coil-kt:coil-compose:2.4.0")

    //ViewModel For Jetpack Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.compose.material3:material3:1.1.1")
    //Dagger Hilt Annotation Processor
    kapt("com.google.dagger:hilt-compiler:2.47")

    //Coroutines Core Package
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    //Coroutines Provides Dispatchers.Main and Logs Unhandled Exceptions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //OkHttp Http Client For Kotlin & Java
    implementation("com.squareup.okhttp3:okhttp:$okHttp")
    //OkHttp Logs HTTP Requests & Responses
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttp")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //// Testing Dependencies ////

    //Mockk Mocking For Kotlin
    testImplementation("io.mockk:mockk:$mockkVersion")
    //Mockk Mocking For Kotlin with Android
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")

    //Junit5 For Writing Tests In Junit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    //Junit5 Core Package
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    //Junit5 For Parameterised Tests
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jUnitVersion")

    //Coroutines Provides Utilities For Testing Coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    //Robolectric For UnitTesting The Android Framework
    testImplementation("org.robolectric:robolectric:4.7.3")

}