plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.hilt)
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    namespace = "com.example.wavecast.core.network"

    buildFeatures {
        buildConfig = true
    }

}

secrets {
    propertiesFileName = "secret.properties"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.android)
    
    // JSON Serialization
    implementation(libs.kotlinx.serialization.json)
}
