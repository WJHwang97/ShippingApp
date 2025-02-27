plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.rfidwriter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rfidwriter"
        minSdk = 30
        targetSdk = 34
        versionCode = 13
        versionName = "1.0.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true // BuildConfig 활성화
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        encoding = "UTF-8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\scan_sdk.jar"))
    implementation(files("libs\\serial_sdk.jar"))
    implementation(files("libs\\rfid_sdk.jar"))
    implementation(files("libs\\jtds.jar"))
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // androidx.media3 라이브러리 하위 버전 사용
    implementation("androidx.media3:media3-exoplayer:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
