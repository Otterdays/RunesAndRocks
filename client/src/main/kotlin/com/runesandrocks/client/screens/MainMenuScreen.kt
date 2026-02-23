package com.runesandrocks.client.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.runesandrocks.client.ClientLauncher

class MainMenuScreen(val game: ClientLauncher) : ScreenAdapter() {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin()
    private val font = BitmapFont()

    init {
        // Create basic UI skin manually — use distinct names to avoid Skin overwrites
        skin.add("font", font)

        val btnPixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.valueOf("#5e81ac"))
            fill()
        }
        skin.add("button-up", Texture(btnPixmap))
        btnPixmap.dispose()

        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.valueOf("#4c566a"))
            fill()
        }
        skin.add("background", Texture(bgPixmap))
        bgPixmap.dispose()

        val cursorPixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.WHITE)
            fill()
        }
        skin.add("cursor", Texture(cursorPixmap))
        cursorPixmap.dispose()

        val textButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.getDrawable("button-up")
            down = skin.getDrawable("background")
            font = skin.getFont("font")
        }
        skin.add("default", textButtonStyle)

        val textFieldStyle = TextField.TextFieldStyle().apply {
            font = skin.getFont("font")
            fontColor = Color.WHITE
            background = skin.getDrawable("background")
            cursor = skin.getDrawable("cursor")
        }
        skin.add("default", textFieldStyle)

        val labelStyle = Label.LabelStyle().apply {
            font = skin.getFont("font")
        }
        skin.add("default", labelStyle)

        // Setup UI
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val title = Label("Runes & Rocks", skin)
        title.setFontScale(2f)
        table.add(title).padBottom(40f).row()
        
        table.add(Label("Username:", skin)).padBottom(10f).row()
        val usernameField = TextField("PlayerOne", skin)
        table.add(usernameField).width(200f).height(40f).padBottom(20f).row()

        val playWorld1Btn = TextButton("Play World 1", skin)
        playWorld1Btn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game, usernameField.text, game.defaultHost)
            }
        })
        table.add(playWorld1Btn).width(200f).height(50f).padBottom(20f).row()
        
        val exitBtn = TextButton("Exit", skin)
        exitBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })
        table.add(exitBtn).width(200f).height(50f).row()

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.25f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
