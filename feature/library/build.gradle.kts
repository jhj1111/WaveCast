plugins {
    alias(libs.plugins.wavecast.android.feature)
}

android {
    namespace = "com.example.wavecast.feature.library"
}

dependencies {
    // Dependencies are mostly handled by the feature convention plugin
}
