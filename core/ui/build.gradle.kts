plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.library.compose)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.core.ui"
}

dependencies {
    // Add common UI dependencies here
}
