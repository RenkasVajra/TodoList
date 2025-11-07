//val org.gradle.accessors.dm.LibrariesForLibs.json: kotlin.Any
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 28
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }

}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.compose.material3:material3-icons-extended:1.1.1")
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.json)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.saveable)
    //implementation(libs.androidx.datastore.core.jvm)
    implementation(libs.firebase.dataconnect)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.tony19:logback-android:3.0.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material:1.9.4")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation(platform("androidx.compose:compose-bom:2025.10.01"))
}
