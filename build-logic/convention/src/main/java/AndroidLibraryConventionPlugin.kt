import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.example.wavecast.configureKotlinAndroid
import com.example.wavecast.disableUnnecessaryAndroidTests
import com.example.wavecast.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")

                extensions.configure<LibraryExtension> {
                    configureKotlinAndroid(this)
                    testOptions.targetSdk = 36
                }
                extensions.configure<LibraryAndroidComponentsExtension> {
                    disableUnnecessaryAndroidTests(target)
                }
                dependencies {
                    "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                    "androidTestImplementation"(libs.findLibrary("junit").get())
                    "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())

                    "testImplementation"(libs.findLibrary("kotlin.test").get())
                    "testImplementation"(libs.findLibrary("junit").get())

                    "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
                }

            }

        }
    }
}
