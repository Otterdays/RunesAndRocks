pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "RunesAndRocks"

include("shared")
include("server")
include("client")
// Uncomment when opening in Android Studio or when ANDROID_HOME is set:
// include("android")
