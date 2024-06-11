plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.myapplication_soco"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication_soco"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BoM (Bill of Materials) - no need to specify versions for Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Firebase libraries
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // FirebaseUI libraries
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

//    // CameraX libraries
//    implementation("androidx.camera:camera-camera2:1.3.3")
//    implementation("androidx.camera:camera-lifecycle:1.3.3")
//    implementation("androidx.camera:camera-view:1.3.3")
//
//    // Guava library
//    implementation("com.google.guava:guava:33.2.1-android")
//
//    // Example library with exclusion (assuming you need it, otherwise remove)
//    implementation("com.example:some-library:1.0.0") {
//        exclude(group = "com.google.firebase.crashlytics.buildtools.reloc.com.google.common", module = "guava")
//    }

}