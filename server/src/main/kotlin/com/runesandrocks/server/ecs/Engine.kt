package com.runesandrocks.server.ecs

import kotlin.reflect.KClass

typealias EntityId = Int

interface Component

class Engine {
    private var nextEntityId: EntityId = 1
    private val activeEntities = mutableSetOf<EntityId>()
    val entityCount: Int get() = activeEntities.size
    
    private val components = mutableMapOf<KClass<out Component>, MutableMap<EntityId, Component>>()
    private val systems = mutableListOf<System>()

    fun createEntity(): EntityId {
        val id = nextEntityId++
        activeEntities.add(id)
        return id
    }

    fun destroyEntity(entity: EntityId) {
        if (activeEntities.remove(entity)) {
            components.values.forEach { it.remove(entity) }
        }
    }

    fun addComponent(entity: EntityId, component: Component) {
        val type = component::class
        val map = components.getOrPut(type) { mutableMapOf() }
        map[entity] = component
    }

    fun removeComponent(entity: EntityId, componentClass: KClass<out Component>) {
        components[componentClass]?.remove(entity)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> getComponent(entity: EntityId, componentClass: KClass<T>): T? {
        return components[componentClass]?.get(entity) as? T
    }

    fun addSystem(system: System) {
        systems.add(system)
    }

    private val taskQueue = java.util.concurrent.ConcurrentLinkedQueue<() -> Unit>()
    val taskQueueDepth: Int get() = taskQueue.size

    fun queueTask(task: () -> Unit) {
        taskQueue.add(task)
    }

    fun update(delta: Float) {
        // Drain network actions safely on the tick thread
        while (true) {
            val task = taskQueue.poll() ?: break
            task()
        }

        systems.forEach { it.update(delta) }
    }

    fun getEntitiesWith(vararg componentClasses: KClass<out Component>): Set<EntityId> {
        if (componentClasses.isEmpty()) return emptySet()
        val firstMap = components[componentClasses[0]] ?: return emptySet()
        var result = firstMap.keys.toSet()
        for (i in 1 until componentClasses.size) {
            val map = components[componentClasses[i]] ?: return emptySet()
            result = result.intersect(map.keys)
            if (result.isEmpty()) break
        }
        return result
    }
}
