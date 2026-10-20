plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.fly8.clone"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fly8.app"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-beta01"
    }

    signingConfigs {
        // Public beta test key so successive beta builds install over each other.
        // Not a production key — see keystore/README.md.
        create("beta") {
            storeFile = file("../keystore/fly8-beta.jks")
            storePassword = "fly8beta"
            keyAlias = "fly8beta"
            keyPassword = "fly8beta"
        }
    }

    buildTypes {
        debug { signingConfig = signingConfigs.getByName("beta") }
        release {
            signingConfig = signingConfigs.getByName("beta")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Wireless debugging (ADB-over-TLS pairing via SPAKE2) — primary control path.
    implementation("com.github.MuntashirAkon:libadb-android:3.1.1")
    implementation("com.github.MuntashirAkon:sun-security-android:1.1")
    implementation("org.conscrypt:conscrypt-android:2.5.3")

    // Optional alternative control path. Never required.
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    testImplementation("junit:junit:4.13.2")
}
