import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maps.plugin)
}

android {
    namespace = "com.gunkel.android.drift"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gunkel.android.drift"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
//            applicationIdSuffix = "debug"

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    secrets {
        propertiesFileName = ".secrets/debug.properties"
        defaultPropertiesFileName = ".secrets/local.properties"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":feature:map:ui"))
    implementation(project(":feature:map:domain"))
    implementation(project(":feature:map:data"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.compat)
    implementation(libs.material)
    implementation(libs.activity.compose)
    implementation(libs.androidx.startup)
    implementation("io.insert-koin:koin-androidx-startup:4.2.1")
    implementation(libs.koin.android)

    testImplementation(libs.junit)

    testImplementation(libs.junit)
    implementation(platform(libs.compose.bom))
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    implementation(libs.androidx.runtime) // Use a recent and compatible version
}
