plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.runesandrocks.client.android"
    compileSdk = 34
    
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
            assets.srcDirs("src/main/assets", "../../client/src/main/resources")
        }
    }

    defaultConfig {
        applicationId = "com.runesandrocks.client"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":client")) {
        // Exclude desktop-only dependencies provided by client module
        exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl3")
        exclude(group = "com.badlogicgames.gdx", module = "gdx-platform")
    }
    
    val gdxVersion = "1.12.1"
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}
