plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}


android {
    namespace = "it.spindox.navigation"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions.add("environment")

    productFlavors {
        create("mock") {
            dimension = "environment"
        }
        create("dev") {
            dimension = "environment"
        }
        create("uat") {
            dimension = "environment"
        }
        create("prod") {
            dimension = "environment"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:common"))

    ksp(libs.hilt.compiler)
    implementation(libs.bundles.hilt)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp.interceptor)
    implementation(libs.tasks.genai)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}