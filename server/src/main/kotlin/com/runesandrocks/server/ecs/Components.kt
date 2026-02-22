package com.runesandrocks.server.ecs

data class Position(var x: Float, var y: Float) : Component
data class Velocity(var dx: Float, var dy: Float) : Component
