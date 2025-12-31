plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "it.spindox.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", "\"https://pokeapi.co\"")
    }

    flavorDimensions.add("environment")

    productFlavors {
        create("mock") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://pokeapi.co\"")
        }

        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://pokeapi.co\"")
        }
        create("uat") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://pokeapi.co\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://pokeapi.co\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
        debug {

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
    implementation(project(":core:common"))

    ksp(libs.hilt.compiler)
    implementation(libs.bundles.hilt)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.interceptor)
    implementation(libs.tasks.genai)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
}