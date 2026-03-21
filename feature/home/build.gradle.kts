plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.library.compose)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.feature.home"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
}
