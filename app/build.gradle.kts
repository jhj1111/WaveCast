plugins {
    alias(libs.plugins.wavecast.android.application)
    alias(libs.plugins.wavecast.android.application.compose)
    alias(libs.plugins.wavecast.android.hilt)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.example.wavecast"

    defaultConfig {
        applicationId = "com.example.wavecast"
        versionCode = 1
        versionName = "0.0.0" // X.Y.Z; X = Major, Y = minor, Z = Patch level

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
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":feature:home"))
    implementation(project(":feature:player"))
    implementation(project(":feature:library"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // Baseline Profile
    baselineProfile(project(":benchmark"))
}

baselineProfile {
    // Our benchmark module is called :benchmark
    filter {
        include("com.example.wavecast.**")
    }
}
