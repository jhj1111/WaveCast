plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.library.compose)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.core.ui"
}

dependencies {
    // material3
    implementation(libs.androidx.compose.material.icons.extended)
    // coil3
    implementation(libs.coil.compose)
}
