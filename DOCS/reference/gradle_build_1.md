<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

Setting up a multi-module Gradle project for the first time can be a bit of a headache, but getting it right from day one is the most important architectural decision you'll make for this engine.

We are going to use **Kotlin DSL** (`build.gradle.kts`) because it provides fantastic auto-complete and type-safety in IntelliJ IDEA.

Here is your folder structure. You will create five files in total:

```text
my-mmo-engine/
├── settings.gradle.kts
├── build.gradle.kts
├── shared/
│   └── build.gradle.kts
├── server/
│   └── build.gradle.kts
└── client/
    └── build.gradle.kts

```

Here is the exact code to drop into each file to get your engine's skeleton connected.

### 1. The Root Settings (`settings.gradle.kts`)

This file lives in the root folder and simply tells Gradle which modules exist in the project.

```kotlin
rootProject.name = "MyMMOEngine"

include("shared")
include("server")
include("client")

```

### 2. The Root Build Script (`build.gradle.kts`)

This also lives in the root directory. It defines the global configuration (like the Kotlin version) so you don't have to repeat it in every sub-module. We use `apply false` here because the root project itself doesn't compile any Kotlin code.

```kotlin
plugins {
    // Use the latest Kotlin version available to you
    kotlin("jvm") version "1.9.22" apply false 
}

allprojects {
    repositories {
        mavenCentral()
    }
}

```

### 3. The Shared Module (`shared/build.gradle.kts`)

This module is a plain library. It doesn't run on its own; it just holds your math, enums, and packet data classes.

```kotlin
plugins {
    kotlin("jvm")
}

dependencies {
    // Put libraries here that BOTH the client and server need.
    // Example: A fast serialization library for your network packets
    implementation("com.esotericsoftware:kryo:5.6.0")
}

```

### 4. The Server Module (`server/build.gradle.kts`)

This is an executable application. Notice the crucial `project(":shared")` line—that is the magic that lets your server read the packets defined in the shared module.

```kotlin
plugins {
    kotlin("jvm")
    application
}

application {
    // The entry point for your headless server
    mainClass.set("com.myengine.server.ServerLauncherKt")
}

dependencies {
    // 1. Link to the Shared module
    implementation(project(":shared"))
    
    // 2. Server-only dependencies (Networking, Database, etc.)
    implementation("io.ktor:ktor-network:2.3.7") // For TCP sockets
    implementation("org.slf4j:slf4j-simple:2.0.12") // For console logging
}

```

### 5. The Client Module (`client/build.gradle.kts`)

This is also an executable application, but it holds all your visual code. It also gets access to the shared module.

```kotlin
plugins {
    kotlin("jvm")
    application
}

application {
    // The entry point for your visual game window
    mainClass.set("com.myengine.client.ClientLauncherKt")
}

dependencies {
    // 1. Link to the Shared module
    implementation(project(":shared"))
    
    // 2. Client-only dependencies (Rendering, Audio, Input)
    // Example: LibGDX core and desktop backend
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-desktop")
}

```

---

### What to do next:

Once you have these files created, open the root folder in IntelliJ IDEA and let Gradle sync. You will immediately have a clean, modular workspace.

Would you like to write the fixed-timestep game loop for the server next, or should we set up the first basic network connection between the client and server?
