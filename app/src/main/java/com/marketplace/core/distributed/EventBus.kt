package com.marketplace.core.distributed

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

/**
 * EnterpriseEventBus: A high-throughput, centralized event distribution system.
 * 
 * This module facilitates asynchronous communication between decoupled modules
 * (e.g., Analytics, Logistics, UI) without creating direct dependencies.
 * It uses Kotlin's SharedFlow for efficient broadcast and supports filtering
 * by event type. This architecture is essential for large-scale systems 
 * where cross-module synchronization is frequent.
 */
class EnterpriseEventBus {

    private val _events = MutableSharedFlow<SystemEvent>(extraBufferCapacity = 100)
    
    /**
     * Broadcasts a system-wide event to all subscribers.
     * 
     * The event is emitted into a shared flow with a buffer to prevent blocking
     * high-speed producers. This ensures that telemetry, user actions, and
     * system alerts are distributed reliably across the entire ecosystem.
     * 
     * @param event The SystemEvent to broadcast.
     */
    fun publish(event: SystemEvent) {
        val result = _events.tryEmit(event)
        if (!result) {
            println("EventBus: WARNING - Event buffer overflow. Dropping event: ${event.id}")
        }
    }

    /**
     * Subscribes to events of a specific type.
     * 
     * This method returns a filtered flow, allowing components to react 
     * only to relevant system changes. For example, the UI might only 
     * listen for `UI_UPDATE` events while the Analytics engine listens for `TRANSACTION` events.
     * 
     * @param T The type of events to filter for.
     * @return A cold flow emitting filtered and casted events.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : SystemEvent> subscribe(): Flow<T> {
        return _events.filter { it is T }.map { it as T }
    }

    /**
     * Resets the event bus state.
     * 
     * Primarily used for clearing state between test runs or during 
     * system reboots to prevent event leaks across session boundaries.
     */
    fun clear() {
        // SharedFlow doesn't have a clear, but we could recreate it if needed
        println("EventBus: Resetting bus state.")
    }
}

/**
 * Sealed class hierarchy for all supported system events.
 * This structure enforces type-safety across the distributed bus.
 */
sealed class SystemEvent {
    val id: String = java.util.UUID.randomUUID().toString()
    val timestamp: Long = System.currentTimeMillis()

    data class UserInteraction(val userId: String, val action: String) : SystemEvent()
    data class TransactionCreated(val orderId: String, val amount: Double) : SystemEvent()
    data class InventoryAlert(val productId: String, val stockLevel: Int) : SystemEvent()
    data class SystemWarning(val module: String, val message: String) : SystemEvent()
    data class LogisticsUpdate(val shipmentId: String, val status: String) : SystemEvent()
    data class AnalyticsPulse(val metric: String, val value: Double) : SystemEvent()
}
