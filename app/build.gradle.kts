plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // Required for Room Database processing
}

android {
    namespace = "com.yourdomain.webwallpaper"
        compileSdk = 35

    defaultConfig {
        applicationId = "com.yourdomain.webwallpaper"
        minSdk = 26 // Minimum required for modern WebView and ExoPlayer features
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Jetpack Compose BOM (Bill of Materials)
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Media3 (ExoPlayer) for Video Wallpaper
    val media3Version = "1.3.0"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    // Room Database for History Grid
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore for saving Scale Mode and Battery settings
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // WebKit for extended WebView capabilities
    implementation("androidx.webkit:webkit:1.10.0")
}
