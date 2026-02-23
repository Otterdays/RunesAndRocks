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
            jniLibs.srcDir("libs")
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

val natives by configurations.creating

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
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

tasks.register("copyAndroidNatives") {
    doFirst {
        file("libs/armeabi-v7a/").mkdirs()
        file("libs/arm64-v8a/").mkdirs()
        file("libs/x86_64/").mkdirs()
        file("libs/x86/").mkdirs()

        natives.files.forEach { jar ->
            var outputDir: File? = null
            if (jar.name.endsWith("natives-arm64-v8a.jar")) outputDir = file("libs/arm64-v8a")
            if (jar.name.endsWith("natives-armeabi-v7a.jar")) outputDir = file("libs/armeabi-v7a")
            if (jar.name.endsWith("natives-x86_64.jar")) outputDir = file("libs/x86_64")
            if (jar.name.endsWith("natives-x86.jar")) outputDir = file("libs/x86")
            
            if (outputDir != null) {
                copy {
                    from(zipTree(jar))
                    into(outputDir)
                    include("*.so")
                }
            }
        }
    }
}

tasks.configureEach {
    if (name.contains("merge") && name.contains("JniLibFolders")) {
        dependsOn("copyAndroidNatives")
    }
}
