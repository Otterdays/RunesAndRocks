plugins {
    kotlin("jvm") version "2.3.0" apply false
    kotlin("android") version "1.9.0" apply false
    id("com.android.application") version "8.1.1" apply false
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

