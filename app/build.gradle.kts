plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "vn.dihaver.tech.bank.widget"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.dihaver.tech.bank.widget"
        minSdk = 23
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
}

dependencies {

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.blurview)
    implementation(libs.core)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.zxing.android.embedded)

}