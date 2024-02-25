import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

val composeVersion = "1.6.1"
val lifecycleVersion = "2.7.0"
val retrofitVersion = "2.9.0"
val jUnitVersion = "5.8.2"
val mockkVersion = "1.13.9"
val okHttp = "4.11.0"
val hiltVersion = "2.50"

android {
    namespace = "com.tinnovakovic.hiking"
    compileSdk = 34

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.tinnovakovic.hiking"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "FLICKR_API_KEY", "\"${properties.getProperty("FLICKR_API_KEY")}\"")
        buildConfigField("String", "FLICKR_SECRET", "\"${properties.getProperty("FLICKR_SECRET")}\"")
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
        kotlinCompilerExtensionVersion = "1.5.0"
    }

}

dependencies {

    implementation("com.google.android.gms:play-services-location:21.1.0")


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    //Location Services
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.android.gms:play-services-base:18.3.0")


    //Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-unit:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
//    implementation("androidx.compose.compiler:compiler:1.5.9")


    //Jetpack Compose Material Design Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("io.coil-kt:coil-compose:2.4.0")

    //ViewModel For Jetpack Compose

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    kapt ("com.google.dagger:hilt-compiler:$hiltVersion")

    // For instrumentation tests
    androidTestImplementation  ("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:$hiltVersion")

    // For local unit tests
    testImplementation ("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptTest ("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.compose.material3:material3:1.2.0")


    //Coroutines Core Package
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    //Coroutines Provides Dispatchers.Main and Logs Unhandled Exceptions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("app.cash.turbine:turbine:1.0.0")
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

    //Coroutines Provides Utilities For Testing Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:1.8.0")

    //Robolectric For UnitTesting The Android Framework
    testImplementation("org.robolectric:robolectric:4.7.3")

}

kapt {
    correctErrorTypes = true
}
//    configurations.implementation {
//        //TODO: This is a working solution, but it would be better to find where
//        //this dependency duplication is coming from
//        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8") }
