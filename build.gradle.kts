plugins {
    kotlin("jvm") version "2.3.10" apply false
    id("com.android.application") version "9.0.1" apply false
}

allprojects {
    group = "com.runesandrocks"
    version = "0.0.1"

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    // CVE-2025-68161: force Log4j 2.25.3+ if pulled in transitively (e.g. by HikariCP)
    configurations.all {
        resolutionStrategy {
            force("org.apache.logging.log4j:log4j-core:2.25.3")
        }
    }
}

