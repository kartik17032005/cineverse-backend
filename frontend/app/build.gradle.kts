plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cineverse_movie_app_two"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cineverse_movie_app_two"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation(libs.retrofit)
    implementation("androidx.preference:preference:1.2.0")
    implementation(libs.converter.gson)
    implementation(libs.minavdrawer)
    implementation(libs.lottie)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.2")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation(libs.volley)
    implementation(libs.transition)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}