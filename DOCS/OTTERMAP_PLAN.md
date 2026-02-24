# OtterMap — World Builder & Asset Pipeline

The complete plan for going from raw `.png` sprite sheets to a live, explorable, server-authoritative multiplayer world. Think RPG Maker meets Tiled, but built entirely for our stack.

---

## Current Reality (Where We Are Now)

- `world.json` is a flat integer array (`0` = walkable, `1` = solid wall).
- `WorldMap.kt` on the server parses it and resolves tile IDs to collision.
- `GameScreen.kt` on the client reads the same `world.json` and paints colored squares via `ShapeRenderer`.
- The map is **20×15 tiles**, hardcoded, no visual distinction between tile types beyond color.
- There is no tooling. To change the map you edit a JSON array of numbers by hand.

**Goal:** Replace this with a full authoring pipeline. Design a map visually, export it, and the server hot-reloads it immediately. Players see real sprites, not colored squares.

---

## Architecture Overview

The pipeline has three distinct layers:

```
[Assets]         →   [OtterMap Editor]   →   [world.json]   →   [Server + Client]
PNG sprite sheets    Browser-based tool       Structured          Runtime parse +
Texture atlases      (runs locally)           world format        collision + rendering
```

No external dependencies. No Tiled license, no Godot export plugin. We own the whole chain.

---

## Layer 1: World Format Upgrade

The existing `world.json` is too simple. We need a multi-layer, metadata-rich format.

### Target Format: `world.json` v2

```json
{
  "meta": {
    "name": "World 1 - Verdant Hollow",
    "version": 2,
    "width": 50,
    "height": 40,
    "tileSize": 16,
    "tileset": "tileset_overworld.png",
    "tilesetCols": 16
  },
  "layers": [
    {
      "name": "ground",
      "type": "tile",
      "visible": true,
      "tiles": [3, 3, 3, 4, 4, 4, ...]
    },
    {
      "name": "objects",
      "type": "tile",
      "visible": true,
      "tiles": [0, 0, 12, 0, 0, ...]
    },
    {
      "name": "collision",
      "type": "collision",
      "tiles": [0, 0, 1, 0, 0, ...]
    }
  ],
  "entities": [
    { "type": "spawn", "x": 5, "y": 5 },
    { "type": "npc", "id": "merchant_01", "x": 10, "y": 8 },
    { "type": "chest", "x": 18, "y": 3, "contents": "gold:50" }
  ],
  "zones": [
    { "name": "town_square", "x": 4, "y": 4, "w": 8, "h": 8 },
    { "name": "danger_zone", "x": 30, "y": 20, "w": 10, "h": 10, "pvp": true }
  ]
}
```

**Key additions over v1:**
- `meta.tileset` — references the sprite sheet by filename
- `meta.tilesetCols` — how many tiles wide the atlas is, so tile ID maps to a pixel rect
- `layers[]` — ground layer, object layer, and explicit collision layer are separate
- `entities[]` — spawn points, NPC placements, chests, doors, triggers
- `zones[]` — named regions with property overrides (PvP flags, music zones, etc.)

---

## Layer 2: OtterMap — The Browser-Based Editor

A standalone HTML/JS editor that runs locally. No install. Open `ottermap/index.html` in any browser, edit the map, hit Export, and a new `world.json` lands in your resources folder (or downloads to your desktop if serving from file://).

### Core Editor Features

#### Viewport
- Zoomable canvas (`Canvas 2D API`) rendering the tile grid.
- Checkerboard background for transparent tiles.
- Grid overlay toggle.
- Camera pan with middle-click or Space+drag (like Photoshop).

#### Tileset Panel (Right Sidebar)
- Load a tileset `.png` from disk via `<input type="file">`.
- Displays the tileset sliced into a clickable grid based on `tileSize`.
- Click a tile to select it. Shift+click to select a rectangular region (stamp brush).

#### Layer Panel
- Show/hide individual layers.
- Lock layers to prevent accidental edits.
- Layer ordering (render order = array order).
- Add/remove layers. Rename layers.

#### Tools
| Tool | Shortcut | Behavior |
|------|----------|----------|
| Paint | B | Single tile paint |
| Fill Bucket | G | Flood-fill contiguous matching tiles |
| Erase | E | Set tile to 0 (transparent) |
| Rect | R | Fill a rectangular region |
| Select | S | Marquee select, copy/paste region |
| Entity | N | Place an entity marker (spawn, NPC, chest) |
| Zone | Z | Draw a named rectangular zone |
| Eyedropper | Alt | Pick tile ID from the map |

#### Entity Palette
- List of entity types with icons.
- Click to place, right-click to edit properties (NPC dialogue, chest contents, trigger script name).
- Entities show as labeled colored icons on the canvas.

#### Export / Import
- **Export** button → generates `world.json` v2 and triggers a browser download.
- **Import** button → load an existing `world.json` to continue editing.
- **Quick Save** → stores to `localStorage` so work survives browser refresh.

---

## Layer 3: Asset Pipeline (Sprite Sheet → Tileset)

Before the editor can show anything, you need a tileset. This is the art → engine bridge.

### Step A: Source Art

Your sprites live in `client/src/main/resources/`. The goal is to consolidate them into a single **tileset PNG** — one image, sliced into a grid of 16×16 (or 32×32) tiles.

Example layout for a 16-col tileset at 16×16 px per tile:
```
Row 0: Grass variants (IDs 0-15)
Row 1: Dirt/path variants (IDs 16-31)
Row 2: Water/coast (IDs 32-47)
Row 3: Walls, rocks (IDs 48-63)
Row 4: Trees, tall grass (IDs 64-79)
Row 5: Interior floors (IDs 80-95)
Row 6: Interior walls (IDs 96-111)
Row 7: Objects: chests, barrels, signs (IDs 112-127)
```

Tile ID is simply `row * tilesetCols + col`. Dead simple to compute. No atlas packer needed for tilemaps.

### Step B: Tileset Registration

When you add a new tileset PNG, register it in `Assets.kt`:
```kotlin
const val TILESET_OVERWORLD = "tileset_overworld.png"

fun loadAll() {
    manager.load(TILESET_OVERWORLD, Texture::class.java)
}
```

### Step C: Client Tile Renderer

Replace the `ShapeRenderer` colored squares in `GameScreen.kt` with a proper tile renderer:

```
TileRenderer.kt
  - Takes WorldMap + Texture atlas reference
  - For each tile layer, computes the source rect from tile ID:
      srcX = (tileId % tilesetCols) * tileSize
      srcY = (tileId / tilesetCols) * tileSize
  - Uses SpriteBatch.draw(texture, dstX, dstY, tileSize, tileSize, srcX, srcY, tileSize, tileSize, ...)
  - Renders ground layer first, then object layer on top
  - Skips tile ID 0 (transparent)
```

### Step D: Camera System

Right now the client has no camera — the world is drawn from (0,0). You need a camera that follows the local player:

```
Camera.kt (LibGDX OrthographicCamera)
  - camera.position.set(playerX, playerY, 0)
  - camera.update()
  - batch.projectionMatrix = camera.combined
  - Optional: lerp smoothing so camera glides instead of snapping
```

---

## Layer 4: Server-Side WorldMap v2 Parser

`WorldMap.kt` needs to understand the new format: multiple layers, entity placements, and zones.

**Key additions to `WorldMap.kt`:**
```kotlin
data class EntityMarker(val type: String, val x: Float, val y: Float, val properties: Map<String, String>)
data class Zone(val name: String, val x: Int, val y: Int, val w: Int, val h: Int, val properties: Map<String, String>)

class WorldMap(mapJson: String) {
    val meta: WorldMeta
    val groundLayer: List<Int>
    val collisionLayer: List<Int>   // explicit, replaces the tile!=0 hack
    val entities: List<EntityMarker>
    val zones: List<Zone>
    
    fun isSolid(worldX, worldY): Boolean  // reads collisionLayer, not tile ID
    fun getZoneAt(worldX, worldY): Zone?
    fun getSpawnPoints(): List<EntityMarker>
}
```

The server reads `entities` to place NPCs, spawn players at marked spawn points, and apply zone rules (PvP flags, etc.) during `MovementSystem` tick.

---

## Layer 5: Hot Reload Pipeline

After you paint in OtterMap and export, the world updates **without restarting the server**:

1. You click Export in OtterMap → `world.json` saved to the resources folder.
2. Admin Dashboard has a **"Reload World"** button (from `SERVER_UI_UPGRADE.md` item #14).
3. Button calls `POST /api/actions/world/reload`.
4. Server queues a task on the ECS engine thread:
   - Re-reads `world.json` from disk.
   - Rebuilds the `WorldMap` instance.
   - Re-registers entity spawn markers.
   - All currently connected players stay connected — the world silently updates beneath them.
5. A `WorldReload` packet is broadcast to all clients.
6. Client `GameScreen` re-loads the `WorldMap` from the server's new state on next render.

---

## Delivery Order (Implementation Phases)

| Phase | Deliverable | Effort |
|-------|-------------|--------|
| **M1** | World JSON v2 format spec + upgrade `WorldMap.kt` parser | Low |
| **M2** | `TileRenderer.kt` on client + `OrthographicCamera` | Low-Medium |
| **M3** | OtterMap editor core: canvas, tileset panel, paint/erase tools, export | Medium |
| **M4** | OtterMap: layer system, fill bucket, rect tool, undo/redo | Medium |
| **M5** | OtterMap: entity placement + zone drawing | Medium |
| **M6** | Server hot-reload endpoint + Admin Dashboard button | Low |
| **M7** | Collision layer pass: separate collision from visual tiles | Low |
| **M8** | Camera system with lerp smoothing | Low |

**Start with M1 + M2.** They are pure code with zero UI work and immediately make the game look real instead of colored squares.

---

## Files To Create / Modify

| File | Action |
|------|--------|
| `server/src/main/resources/world.json` | Upgrade to v2 format |
| `server/.../world/WorldMap.kt` | Parse v2: layers, entities, zones |
| `client/.../screens/GameScreen.kt` | Integrate TileRenderer + Camera |
| `client/.../rendering/TileRenderer.kt` | New: sprite-based tile rendering |
| `client/.../rendering/Camera.kt` | New: OrthographicCamera wrapper with lerp |
| `client/src/main/resources/tileset_overworld.png` | New: your first consolidated tileset |
| `client/.../assets/Assets.kt` | Register tileset asset |
| `tools/ottermap/index.html` | New: the complete map editor |
| `tools/ottermap/editor.js` | New: canvas engine, tools, export |
| `tools/ottermap/style.css` | New: dark-mode editor UI |
| `server/.../admin/AdminRoutes.kt` | Add `/api/actions/world/reload` endpoint |

---

## Notes & Decisions

- **No Tiled dependency.** OtterMap outputs our own JSON schema. We control every field. Tiled is a great tool but exporting means wrestling with its XML/JSON format and writing a custom importer anyway.
- **No libGDX Scene2D for the editor.** The editor is a browser tool. Faster to iterate, zero rebuild cycle, works on any machine, easy to share.
- **Tile ID 0 = transparent/empty** across all layers. Collision layer has its own binary encoding (0 = walkable, 1 = solid). This decouples visuals from physics.
- **Entities are server-authoritative.** The entity list in `world.json` is used by the server to spawn NPCs. The client only receives ECS sync packets — it never reads entity markers directly.
- **Zones are replicated to clients** for local effects only (music, ambient sound, UI zone name display). All zone rule enforcement (PvP, etc.) is server-side only.
