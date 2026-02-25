package com.runesandrocks.server.world

import com.fasterxml.jackson.databind.ObjectMapper

class WorldMap(mapJson: String) {
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var tileSize: Int = 16
        private set
    var tiles: List<Int> = emptyList()
        private set

    init {
        reload(mapJson)
    }

    fun reload(mapJson: String) {
        val mapper = ObjectMapper()
        val node = mapper.readTree(mapJson)
        width = if (node.has("width")) node.get("width").asInt() else 0
        height = if (node.has("height")) node.get("height").asInt() else 0
        tileSize = if (node.has("tileSize")) node.get("tileSize").asInt() else 16
        
        val tilesArray = node.get("tiles")
        tiles = if (tilesArray != null && tilesArray.isArray) {
            (0 until tilesArray.size()).map { tilesArray.get(it).asInt() }
        } else {
            emptyList()
        }
    }
    
    fun isSolid(worldX: Float, worldY: Float): Boolean {
        // Find grid coordinates
        val gx = (worldX / tileSize).toInt()
        val gy = (worldY / tileSize).toInt()
        
        // Treat borders of the parsed map as solid infinite walls
        if (gx < 0 || gx >= width || gy < 0 || gy >= height) {
            return true 
        }
        
        val tileId = tiles.getOrNull(gy * width + gx) ?: 0
        return tileId != 0 // Assuming 0 is grass, 1 is solid wall
    }
}
