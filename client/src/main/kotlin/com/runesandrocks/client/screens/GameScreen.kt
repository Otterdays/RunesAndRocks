package com.runesandrocks.client.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.JsonReader
import com.runesandrocks.client.ClientLauncher
import com.runesandrocks.client.assets.Assets
import com.runesandrocks.client.network.GameClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

@OptIn(DelicateCoroutinesApi::class)
class GameScreen(val game: ClientLauncher, val username: String, val hostIp: String) : ScreenAdapter() {
    private val logger = LoggerFactory.getLogger("GameScreen")
    
    private val batch = SpriteBatch()
    private val font = BitmapFont().apply { color = Color.WHITE }
    private val playerTexture: Texture = Assets.manager.get(Assets.PLAYER_TEXTURE, Texture::class.java)
    private val shapeRenderer = ShapeRenderer()
    
    // Status text to show connection
    private var connectionStatus = "Connecting..."
    private var lastDx = 0f
    private var lastDy = 0f

    // Map logic
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileSize = 16f
    private val tiles = mutableListOf<Int>()
    
    private val client: GameClient

    init {
        // Map Load
        val jsonFile = Gdx.files.internal("world.json")
        if (jsonFile.exists()) {
            val root = JsonReader().parse(jsonFile)
            mapWidth = root.getInt("width")
            mapHeight = root.getInt("height")
            tileSize = root.getInt("tileSize").toFloat()
            val tilesArray = root.get("tiles")
            for (i in 0 until tilesArray.size) {
                tiles.add(tilesArray[i].asInt())
            }
        }
        
        val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: GameClient.DEFAULT_PORT
        client = GameClient(host = hostIp, port = port)
        
        GlobalScope.launch {
            try {
                client.connect()
                client.startListening()
                
                client.sendLogin(username)
                
                while (true) {
                    client.sendPing()
                    connectionStatus = "Connected (${client.lastLatencyMs}ms)"
                    delay(1000)
                }
            } catch (e: Exception) {
                logger.error("Failed to connect to server", e)
                connectionStatus = "Failed to Connect"
            }
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        var dx = 0f
        var dy = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 150f
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 150f
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 150f
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 150f

        if (client.connected && (dx != lastDx || dy != lastDy)) {
            lastDx = dx
            lastDy = dy
            GlobalScope.launch { client.sendMove(dx, dy) }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screen = MainMenuScreen(game)
        }
        
        val cx = Gdx.graphics.width / 2f
        val cy = Gdx.graphics.height / 2f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (y in 0 until mapHeight) {
            for (x in 0 until mapWidth) {
                val tileId = tiles[y * mapWidth + x]
                if (tileId == 0) {
                    shapeRenderer.color = Color.valueOf("#2d3340") // Floor
                } else {
                    shapeRenderer.color = Color.valueOf("#5e81ac") // Wall
                }
                shapeRenderer.rect(cx + (x * tileSize), cy + (y * tileSize), tileSize, tileSize)
            }
        }
        shapeRenderer.end()

        batch.begin()
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
        font.draw(batch, "Status: $connectionStatus", 10f, Gdx.graphics.height - 30f)
        font.draw(batch, "Press ESC to disconnect and return to menu", 10f, Gdx.graphics.height - 50f)
        
        client.entities.forEach { (id, pos) ->
            val screenX = pos.first + (Gdx.graphics.width / 2f)
            val screenY = pos.second + (Gdx.graphics.height / 2f)
            
            batch.draw(playerTexture, screenX - 8f, screenY - 8f, 16f, 16f)
            
            if (id == client.myEntityId) {
                font.draw(batch, "You", screenX - 10f, screenY + 20f)
            } else {
                font.draw(batch, "P$id", screenX - 10f, screenY + 20f)
            }
        }
        batch.end()
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        playerTexture.dispose()
        shapeRenderer.dispose()
        GlobalScope.launch { 
            try { client.disconnect() } catch (e: Exception) {} 
        }
    }
}
