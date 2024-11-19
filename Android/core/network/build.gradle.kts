import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.ssafy.network"
    compileSdk = 34

    val properties = Properties()
    properties.load(FileInputStream(rootProject.file("local.properties")))

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "BASE_URL", properties.getProperty("baseUrl"))
        buildConfigField("String", "ACCESS_KEY", properties.getProperty("s3AccessKey"))
        buildConfigField("String", "SECRET_KEY", properties.getProperty("s3SecretKey"))
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
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // HTTP Client
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.squareup.logging.interceptor)

    // Paging
    implementation(libs.androidx.paging.runtime)

    api (libs.androidx.room.runtime)

    // S3
    implementation(libs.aws.android.sdk.mobile.client)
    implementation(libs.aws.android.sdk.s3)

    // FCM
    implementation (libs.firebase.messaging.ktx)
    implementation(platform(libs.firebase.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.krossbow.stomp.core)
    implementation(libs.krossbow.websocket.okhttp)
}