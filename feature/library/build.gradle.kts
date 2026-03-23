plugins {
    alias(libs.plugins.wavecast.android.feature)
}

android {
    namespace = "com.example.wavecast.feature.library"
}

dependencies {
    implementation(project(":core:domain"))
}
