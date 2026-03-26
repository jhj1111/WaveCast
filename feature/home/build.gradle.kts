plugins {
    alias(libs.plugins.wavecast.android.feature)
}

android {
    namespace = "com.example.wavecast.feature.home"
}

dependencies {
    implementation(project(":core:domain"))
}
