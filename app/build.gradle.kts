import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

val signingPropertiesFile = rootProject.file("keystore.properties")
val signingProperties = Properties()
if (signingPropertiesFile.isFile) {
    signingPropertiesFile.inputStream().use { signingProperties.load(it) }
}

fun signingProperty(name: String): String =
    signingProperties.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: error("Missing '$name' in ${signingPropertiesFile.path}")

android {
    namespace = "com.mindbend.optical.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mindbend.optical.game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = if (signingPropertiesFile.isFile) {
                signingConfigs.create("release") {
                    storeFile = rootProject.file(signingProperty("storeFile"))
                    storePassword = signingProperty("storePassword")
                    keyAlias = signingProperty("keyAlias")
                    keyPassword = signingProperty("keyPassword")
                }
            } else {
                logger.warn(
                    "Release signing is not configured. Add keystore.properties to create a signed release."
                )
                null
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)

    // Firebase BoM and Analytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
