plugins {
    kotlin("jvm") version "1.9.24" apply false
    kotlin("android") version "1.9.24" apply false
    id("com.android.application") version "8.13.2" apply false
}

allprojects {
    group = "com.runesandrocks"
    version = "0.0.1"

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}

