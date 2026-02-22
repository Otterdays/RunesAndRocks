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
        width = node.get("width").asInt()
        height = node.get("height").asInt()
        tileSize = node.get("tileSize").asInt()
        
        val tilesArray = node.get("tiles")
        tiles = (0 until tilesArray.size()).map { tilesArray.get(it).asInt() }
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
