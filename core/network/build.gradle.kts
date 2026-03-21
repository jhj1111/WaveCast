plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.core.network"
}

dependencies {
    // Retrofit, OkHttp etc.
}
