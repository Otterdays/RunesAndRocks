package com.runesandrocks.client.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture

object Assets {
    val manager = AssetManager()

    // Define asset paths as constants to avoid typos
    const val PLAYER_TEXTURE = "player.png"

    fun loadAll() {
        // Queue all assets for loading here
        manager.load(PLAYER_TEXTURE, Texture::class.java)
        // Add more assets as we go: manager.load("enemy.png", Texture::class.java)
    }

    fun update(): Boolean {
        return manager.update() // Returns true if all assets are loaded
    }

    fun getProgress(): Float {
        return manager.progress // Returns between 0.0 and 1.0
    }

    fun dispose() {
        manager.dispose()
    }
}
