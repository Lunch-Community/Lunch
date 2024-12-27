
plugins {
    id("com.android.application")
}


android {
    namespace = "com.dudu.watchface.example"
    compileSdk = 33 
    defaultConfig {
        applicationId = "com.dudu.watchface.example"
        minSdk = 19
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = false
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    signingConfigs {
        create("Launcher") {
            keyAlias = "launcher"
            keyPassword = "lT.20090708"
            storeFile = file("../launcher.keystore")
            storePassword = "lT.20090708"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("Launcher")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("Launcher")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    //watchface-dev
    implementation(project(":watchface-dev-utils"))
}


