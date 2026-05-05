package com.marketplace.logistics.engine

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * LogisticsSimulationEngine: A high-fidelity real-time delivery tracking and simulation system.
 * 
 * This engine is responsible for simulating the physical movement of delivery personnel across
 * a virtual map. It uses coroutines to manage concurrent "Active Shipments", where each
 * shipment updates its coordinates based on traffic heuristics and delivery priority.
 * The engine provides a stream of "Live Updates" that can be consumed by the UI to show
 * smooth, real-time vehicle movement on a map, simulating a production environment like Uber or DoorDash.
 */
class LogisticsSimulationEngine {

    private val activeShipments = ConcurrentHashMap<String, ShipmentState>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Starts a simulation for a new shipment.
     * 
     * Each shipment is treated as a separate process. The engine calculates the vector between
     * the current position and the destination, then incrementally updates the position
     * at fixed time intervals. It also accounts for "Random Delays" (traffic, accidents)
     * to make the simulation as realistic as possible for stress testing.
     * 
     * @param orderId The ID of the order being delivered.
     * @param start The starting coordinates.
     * @param end The destination coordinates.
     */
    fun startShipment(orderId: String, start: Pair<Double, Double>, end: Pair<Double, Double>) {
        scope.launch {
            val shipmentId = "SHIP-${UUID.randomUUID().toString().take(6)}"
            var currentPos = start
            
            _shipmentUpdates.emit(ShipmentUpdate(orderId, shipmentId, currentPos, "DISPATCHED"))

            while (calculateDistance(currentPos, end) > 0.0001) {
                // Simulate movement
                val nextPos = moveTowards(currentPos, end, 0.005)
                currentPos = nextPos
                
                // Simulate variable delay (traffic)
                val delayTime = (200..1000).random().toLong()
                delay(delayTime)
                
                val status = if (calculateDistance(currentPos, end) < 0.01) "ARRIVING" else "IN_TRANSIT"
                _shipmentUpdates.emit(ShipmentUpdate(orderId, shipmentId, currentPos, status))
            }

            _shipmentUpdates.emit(ShipmentUpdate(orderId, shipmentId, end, "DELIVERED"))
        }
    }

    private val _shipmentUpdates = MutableSharedFlow<ShipmentUpdate>()
    /**
     * A broadcast stream of all active shipment updates.
     * 
     * Observers can collect from this flow to receive real-time updates for any
     * active delivery in the system. This is the primary data source for the
     * "Live Tracking" UI components.
     */
    val shipmentUpdates: SharedFlow<ShipmentUpdate> = _shipmentUpdates.asSharedFlow()

    /**
     * Linear interpolation between two points to simulate smooth movement.
     * 
     * Given a starting point, a target point, and a step size, this function calculates
     * the next coordinate in the sequence. It uses basic vector math to determine the
     * direction and distance of the increment.
     */
    private fun moveTowards(current: Pair<Double, Double>, target: Pair<Double, Double>, step: Double): Pair<Double, Double> {
        val dx = target.first - current.first
        val dy = target.second - current.second
        val dist = Math.sqrt(dx * dx + dy * dy)
        
        if (dist <= step) return target
        
        return (current.first + (dx / dist) * step) to (current.second + (dy / dist) * step)
    }

    /**
     * Calculates the Euclidean distance between two geographic coordinates.
     * 
     * While simple, this function is used hundreds of times per second during
     * high-volume simulations to verify shipment proximity to the destination.
     */
    private fun calculateDistance(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
        return Math.sqrt(Math.pow(p2.first - p1.first, 2.0) + Math.pow(p2.second - p1.second, 2.0))
    }

    /**
     * Forcefully terminates all active simulations.
     * 
     * Used for system resets or when clearing test data. This ensures no orphaned
     * coroutines continue to consume system resources after a session ends.
     */
    fun stopAll() {
        scope.cancel()
        println("LogisticsSimulation: All active shipment simulations terminated.")
    }
}

/**
 * Snapshot of a shipment's state at a specific point in time.
 */
data class ShipmentUpdate(
    val orderId: String,
    val shipmentId: String,
    val location: Pair<Double, Double>,
    val status: String
)

/**
 * Internal state for tracking a shipment.
 */
data class ShipmentState(
    val id: String,
    val currentLat: Double,
    val currentLng: Double,
    val destinationLat: Double,
    val destinationLng: Double,
    val lastUpdate: Long
)
