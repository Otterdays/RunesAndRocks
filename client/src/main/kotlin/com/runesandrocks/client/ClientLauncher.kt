package com.runesandrocks.client

import com.badlogic.gdx.Game
import com.runesandrocks.client.screens.LoadingScreen
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.runesandrocks.client.screens.MainMenuScreen
import com.runesandrocks.shared.Shared
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ClientLauncher")

class ClientLauncher(val defaultHost: String = "127.0.0.1") : Game() {
    override fun create() {
        super.setScreen(LoadingScreen(this))
    }
}

fun main() {
    logger.info("[CLIENT] Runes & Rocks Client v{}", Shared.VERSION)
    
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Runes & Rocks Client")
    config.setWindowedMode(800, 600)
    config.useVsync(true)
    config.setForegroundFPS(60)
    
    Lwjgl3Application(ClientLauncher(), config)
}
