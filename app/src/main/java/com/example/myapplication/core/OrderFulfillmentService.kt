package com.example.myapplication.core

import com.example.myapplication.data.model.*
import kotlinx.coroutines.delay
import java.util.*

/**
 * OrderFulfillmentService: Manages the complex state machine of order processing.
 * Implements logic for allocation, picking, packing, and carrier handoff.
 */
class OrderFulfillmentService {

    private val fulfillmentQueue = mutableListOf<FulfillmentTask>()

    suspend fun initiateFulfillment(order: Order) {
        val task = FulfillmentTask(
            id = UUID.randomUUID().toString(),
            orderId = order.id,
            status = FulfillmentStatus.ALLOCATING,
            startedAt = System.currentTimeMillis()
        )
        fulfillmentQueue.add(task)
        
        processTask(task)
    }

    private suspend fun processTask(task: FulfillmentTask) {
        // Step 1: Inventory Allocation
        updateTaskStatus(task, FulfillmentStatus.ALLOCATING)
        delay(500)
        
        // Step 2: Picking
        updateTaskStatus(task, FulfillmentStatus.PICKING)
        delay(800)
        
        // Step 3: Quality Control
        updateTaskStatus(task, FulfillmentStatus.QUALITY_CONTROL)
        delay(400)
        
        // Step 4: Packing
        updateTaskStatus(task, FulfillmentStatus.PACKING)
        delay(600)
        
        // Step 5: Carrier Handoff
        updateTaskStatus(task, FulfillmentStatus.READY_FOR_SHIPMENT)
        println("Fulfillment: Task ${task.id} for Order ${task.orderId} is ready.")
    }

    private fun updateTaskStatus(task: FulfillmentTask, status: FulfillmentStatus) {
        val index = fulfillmentQueue.indexOfFirst { it.id == task.id }
        if (index != -1) {
            fulfillmentQueue[index] = task.copy(status = status)
        }
    }

    fun getActiveTasks(): List<FulfillmentTask> = fulfillmentQueue.filter { 
        it.status != FulfillmentStatus.COMPLETED 
    }
}

data class FulfillmentTask(
    val id: String,
    val orderId: String,
    val status: FulfillmentStatus,
    val startedAt: Long
)

enum class FulfillmentStatus {
    ALLOCATING, PICKING, QUALITY_CONTROL, PACKING, READY_FOR_SHIPMENT, COMPLETED
}
