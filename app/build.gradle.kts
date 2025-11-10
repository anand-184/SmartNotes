import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.anand.smartnotes"
    compileSdk = 36

    buildFeatures {
        compose = true
        buildConfig = true  // Add this
    }

    defaultConfig {
        applicationId = "com.anand.smartnotes"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Temporarily hardcode testing ke liye
        buildConfigField("String", "GEMINI_API_KEY", "\"AIzaSyC5_q1fee-_liJNQ6Y0B7EZ4wPo2_MfRqs\"")
    }


    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }




}

secrets {
    // BuildConfig field का naam
    propertiesFileName = "local.properties"


    // Optional: Ignore missing files
    ignoreList.add("keyToIgnore")
    ignoreList.add("sdk.*")
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.benchmark.traceprocessor.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.constraintlayout.compose)

    implementation(platform("com.google.firebase:firebase-bom:33.3.0")) // 👈 always first

    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")


    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation ("com.cloudinary:cloudinary-android:3.0.2")

    implementation ("androidx.navigation:navigation-compose:2.7.7")




}