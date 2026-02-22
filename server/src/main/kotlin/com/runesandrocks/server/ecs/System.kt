package com.runesandrocks.server.ecs

abstract class System(protected val engine: Engine) {
    abstract fun update(delta: Float)
}
