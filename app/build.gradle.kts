import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "fahrstuhl-app.js"
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.androidx.compose.material.icons.extended)
                implementation(libs.okio)
            }
        }
        
        val nonWasmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.firebase.common)
                implementation(libs.firebase.auth.kmp)
                implementation(libs.firebase.firestore.kmp)
            }
        }

        val androidMain by getting {
            dependsOn(nonWasmMain)
            dependencies {
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.androidx.fragment.ktx)
                implementation(libs.androidx.lifecycle.viewmodel.compose)

                @Suppress("DEPRECATION")
                implementation(project.dependencies.platform(libs.firebase.bom))
                @Suppress("DEPRECATION")
                implementation(libs.firebase.analytics)
                @Suppress("DEPRECATION")
                implementation(libs.firebase.firestore)
                @Suppress("DEPRECATION")
                implementation(libs.firebase.auth)
            }
        }
        
        val wasmJsMain by getting {
            dependencies {
                // Web-spezifische Libs falls nötig
            }
        }
    }
}

@Suppress("DEPRECATION")
android {
    namespace = "com.fableandbytes.fahrstuhl"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fableandbytes.fahrstuhl"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
