plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}

android {
    namespace = "com.dudu.wearlauncher"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.dudu.wearlauncher"
        minSdk = 23
        targetSdk = 33
        versionCode = 10
        versionName = "1.1.5-Stable"
        multiDexEnabled = true
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    signingConfigs {
        
        create("lunch-community") {
            keyAlias = "Lunch Community"
            keyPassword = "lunch-is-the-best"
            storeFile = file("../lunch-community.keystore")
            storePassword = "lunch-is-the-best"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("lunch-community")
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("lunch-community")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = false
        
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildToolsVersion = "34.0.0"

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //SwipeDrawer
    implementation("cn.Leaqi:SwipeDrawer:1.6")
    //glide
    implementation("com.github.bumptech.glide:glide:4.13.2")
    //Multidex
    implementation("androidx.multidex:multidex:2.0.1")
    //UtilCode
    implementation("com.blankj:utilcodex:1.31.1")
    //watchface-dev
    implementation(project(":watchface-dev-utils"))
    //bugly
    implementation("com.tencent.bugly:crashreport:4.1.9")
    //XRadioGroup
    implementation("com.github.fodroid:XRadioGroup:v1.5")
    implementation("androidx.core:core-ktx:1.10.1")
}
