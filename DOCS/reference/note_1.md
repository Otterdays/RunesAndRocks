This is a modern, multi-module Kotlin DSL (build.gradle.kts) setup. This architecture ensures your Client and Server are totally separate but can both see the Shared code (packets, logic).

I have included LibGDX for the client (standard for Java/Kotlin 2D games) and KryoNet for networking (industry standard for Java game networking).

1. settings.gradle.kts
Create this file in the root folder. It tells Gradle what modules exist.

Kotlin
rootProject.name = "MyMMORPG"

include("shared")
include("server")
include("client")
2. Root build.gradle.kts
Create this in the root folder. This file configures the "common" stuff for all modules so you don't have to repeat yourself.

Kotlin
buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        google()
    }
}

// Versions for your tech stack
val gdxVersion = "1.12.1"
val kryoNetVersion = "2.22.0-RC1"
val kotlinVersion = "1.9.22"

// This applies to ALL modules (client, server, shared)
allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")

    group = "com.mygame"
    version = "0.0.1"

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    dependencies {
        // Every module needs Kotlin
        implementation(kotlin("stdlib"))
    }
}

// --- MODULE SPECIFIC CONFIGURATIONS ---

// 1. SHARED: Pure logic, packets, math
project(":shared") {
    dependencies {
        // Networking library (needed here for Packet classes)
        api("com.esotericsoftware:kryonet:$kryoNetVersion")
        
        // Math utilities (Vectors, Shapes) from LibGDX
        api("com.badlogicgames.gdx:gdx:$gdxVersion") 
    }
}

// 2. SERVER: The authoritative game loop
project(":server") {
    dependencies {
        implementation(project(":shared"))
        
        // Headless LibGDX (optional, useful if you need collision logic on server)
        implementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    }
}

// 3. CLIENT: The visual game
project(":client") {
    dependencies {
        implementation(project(":shared"))
        
        // LibGDX Desktop Backend (LWJGL3)
        implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
        implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    }
}
3. Folder Structure
Make sure your file system looks exactly like this for the script to work:

Plaintext
MyMMORPG/
├── build.gradle.kts
├── settings.gradle.kts
├── shared/
│   └── src/main/kotlin/ (Your Packet classes go here)
├── server/
│   └── src/main/kotlin/ (Your ServerLauncher.kt goes here)
└── client/
    └── src/main/kotlin/ (Your ClientLauncher.kt goes here)
Next Step: Verify It Works
Open IntelliJ.

Select File -> Open and pick the root MyMMORPG folder.

IntelliJ will detect the Gradle files and "Import" the project.

Once the loading bars finish, check the Gradle tab on the right side. You should see client, server, and shared listed as tasks.

Would you like the "Hello World" networking code (Server.kt and Client.kt) to test this setup immediately?
