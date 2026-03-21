plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.library.compose)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.feature.library"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
}
