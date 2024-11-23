plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kasolution.aiohunterresources"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kasolution.aiohunterresources"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"

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
    buildFeatures{
        buildConfig = true
        viewBinding=true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    //liveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    //viewmodel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    //fragment
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    //activity
    implementation ("androidx.activity:activity-ktx:1.8.2")
    //refrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //picasso
    implementation ("com.squareup.picasso:picasso:2.71828")
    //compressor
    implementation ("id.zelory:compressor:3.0.1")
    //zoomImage
    implementation ("com.github.MikeOrtiz:TouchImageView:3.6")
    //shimmer facebook efect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    //validar conexion a internet
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    //gson
    implementation("com.google.code.gson:gson:2.10.1")
    //swipe
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //lottiefiles
    implementation ("com.airbnb.android:lottie:4.2.0")
    implementation("androidx.activity:activity:1.8.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}