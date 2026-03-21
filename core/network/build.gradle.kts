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
    // Retrofit, OkHttp etc.
}
