plugins {
    alias(libs.plugins.wavecast.android.library)
    alias(libs.plugins.wavecast.android.hilt)
}

android {
    namespace = "com.example.wavecast.core.domain"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:media"))
    
    implementation(libs.kotlinx.coroutines.android)

    // Test
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}
