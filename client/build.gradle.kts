plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.runesandrocks.client.ClientLauncherKt")
}

dependencies {
    implementation(project(":shared"))
    implementation("io.ktor:ktor-network:3.4.0")
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.12.1")
    implementation("com.badlogicgames.gdx:gdx-platform:1.12.1:natives-desktop")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}
