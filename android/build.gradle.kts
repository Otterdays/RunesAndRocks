plugins {
    id("com.android.application")
}

android {
    namespace = "com.runesandrocks.client.android"
    compileSdk = 35
    
    sourceSets {
        getByName("main") {
            java.directories.clear()
            java.directories.add("src/main/kotlin")
            assets.directories.clear()
            assets.directories.addAll(listOf("src/main/assets", "../../client/src/main/resources"))
        }
    }

    defaultConfig {
        applicationId = "com.runesandrocks.client"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":client")) {
        // Exclude desktop-only dependencies provided by client module
        exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl3")
        exclude(group = "com.badlogicgames.gdx", module = "gdx-platform")
    }

    val gdxVersion = "1.14.0"
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}
