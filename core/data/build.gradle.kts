plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.core.data"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
}
