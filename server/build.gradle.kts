plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.runesandrocks.server.ServerLauncherKt")
}

dependencies {
    implementation(project(":shared"))
    implementation("io.ktor:ktor-network:3.4.0")
    implementation("io.ktor:ktor-server-cio:3.4.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.4.0")
    implementation("io.ktor:ktor-serialization-jackson:3.4.0")
    implementation("io.ktor:ktor-server-websockets:3.4.0")
    implementation("io.ktor:ktor-server-call-logging:3.4.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    
    // Phase 8 Persistence
    implementation("org.jetbrains.exposed:exposed-core:1.0.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0")
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("redis.clients:jedis:7.3.0")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
