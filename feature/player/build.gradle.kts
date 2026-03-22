plugins {
    alias(libs.plugins.wavecast.android.feature)
}

android {
    namespace = "com.example.wavecast.feature.player"
}

dependencies {
//    implementation(project(":core:ui"))
    implementation(project(":core:media"))
}
