plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "uk.hairyfred.openhoy"
    compileSdk = 34

    defaultConfig {
        applicationId = "uk.hairyfred.openhoy"
        // 21 is the floor — Jetpack Compose does not support anything older.
        minSdk = 21
        targetSdk = 34
        // Version can be overridden from the command line / CI:
        //   ./gradlew assembleRelease -PversionName=1.2.3 -PversionCode=42
        versionCode = (project.findProperty("versionCode") as String?)?.toIntOrNull() ?: 1
        versionName = (project.findProperty("versionName") as String?) ?: "1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        // Real release signing is used only when a keystore is supplied via env
        // vars (e.g. from CI secrets). Otherwise the release build falls back to
        // the debug key so local `assembleRelease` still produces an installable
        // APK. See RELEASING.md.
        val keystorePath = System.getenv("OPENHOY_KEYSTORE")
        if (!keystorePath.isNullOrBlank() && file(keystorePath).exists()) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = System.getenv("OPENHOY_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("OPENHOY_KEY_ALIAS")
                keyPassword = System.getenv("OPENHOY_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release")
                ?: signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
