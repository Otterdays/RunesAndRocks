package com.runesandrocks.client.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.runesandrocks.client.ClientLauncher

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false
        
        // 10.0.2.2 is the special Android Emulator alias for Host Machine Loopback (127.0.0.1)
        val host = "10.0.2.2"
        initialize(ClientLauncher(defaultHost = host), config)
    }
}
