package com.runesandrocks.client.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.runesandrocks.client.ClientLauncher
import com.runesandrocks.client.assets.Assets

class LoadingScreen(private val game: ClientLauncher) : ScreenAdapter() {
    private val batch = SpriteBatch()
    private val font = BitmapFont().apply { color = Color.WHITE }
    private val shapeRenderer = ShapeRenderer()

    init {
        // 1) Queue everything!
        Assets.loadAll()
    }

    override fun render(delta: Float) {
        // Clear screen to black
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // 2) Keep feeding the manager
        // Manager.update() returns true when 100% loaded
        val isLoaded = Assets.update() 
        val progress = Assets.getProgress()

        val sw = Gdx.graphics.width.toFloat()
        val sh = Gdx.graphics.height.toFloat()

        // Draw the background Bar empty
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.DARK_GRAY
        val barWidth = 400f
        val barHeight = 40f
        val startX = (sw / 2) - (barWidth / 2)
        val startY = 100f
        shapeRenderer.rect(startX, startY, barWidth, barHeight)
        shapeRenderer.end()

        // Draw the Filled Bar representing progress
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(startX, startY, barWidth * progress, barHeight)
        shapeRenderer.end()

        // Draw loading text percentage
        batch.begin()
        val percentageString = "Loading: ${(progress * 100).toInt()}%"
        font.draw(batch, percentageString, startX, startY + barHeight + 20f)
        batch.end()

        // 3) When 100% loaded, swap to Main Menu
        if (isLoaded) {
            game.screen = MainMenuScreen(game)
        }
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
        // DO NOT dispose Assets here! We are passing loaded assets into the next screens.
    }
}
