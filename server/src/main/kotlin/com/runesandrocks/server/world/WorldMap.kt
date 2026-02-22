package com.runesandrocks.server.world

import com.fasterxml.jackson.databind.ObjectMapper

class WorldMap(mapJson: String) {
    val width: Int
    val height: Int
    val tileSize: Int
    val tiles: List<Int>

    init {
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
        
        val tileId = tiles[gy * width + gx]
        return tileId != 0 // Assuming 0 is grass, 1 is solid wall
    }
}
