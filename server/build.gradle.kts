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
    implementation("org.slf4j:slf4j-simple:2.0.12")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
