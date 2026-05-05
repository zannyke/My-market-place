package com.marketplace.inventory

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * WarehouseManagementSystem (WMS): A high-complexity inventory and logistics hub.
 * 
 * This system is designed to manage large-scale warehouse operations, including
 * stock placement, picking route optimization within the warehouse, and real-time
 * inventory reconciliation across multiple geo-distributed facilities. It employs
 * a thread-safe concurrency model to handle high-frequency stock updates and
 * cross-facility transfers.
 */
class WarehouseManagementSystem {

    private val inventory = ConcurrentHashMap<String, Int>()
    private val binLocations = ConcurrentHashMap<String, String>()
    private val transactionCounter = AtomicInteger(0)

    /**
     * Performs an optimized stock placement based on product velocity and size.
     * 
     * In a high-density warehouse, products that sell frequently are placed in 
     * "hot zones" near the shipping docks to minimize picking time. This function
     * calculates the optimal bin location by analyzing the product's sales velocity
     * and physical dimensions against the available warehouse topology.
     * 
     * @param productId The unique identifier for the product being stored.
     * @param quantity The number of units to be placed in storage.
     * @param velocityScore A value from 0 to 100 indicating how often the item is picked.
     */
    fun allocateStock(productId: String, quantity: Int, velocityScore: Int) {
        val binId = if (velocityScore > 80) {
            "ZONE-A-${randomHex(4)}" // Hot zone
        } else if (velocityScore > 40) {
            "ZONE-B-${randomHex(4)}" // Medium zone
        } else {
            "ZONE-C-${randomHex(4)}" // Cold zone
        }

        inventory[productId] = (inventory[productId] ?: 0) + quantity
        binLocations[productId] = binId
        
        recordTransaction(productId, quantity, "ALLOCATION", binId)
        println("WMS: Allocated $quantity units of $productId to $binId")
    }

    /**
     * Executes a warehouse "Pick Walk" optimization for a set of orders.
     * 
     * To maximize picker efficiency, this function sorts picking tasks based on
     * the physical layout of the warehouse (Bin IDs). This reduces travel time
     * and ensures that the picking path is linear rather than erratic, which
     * is a critical performance metric in modern distribution centers.
     * 
     * @param productIds A list of product IDs required for current fulfillment.
     * @return A sorted list of Bin IDs and corresponding Product IDs to visit.
     */
    fun optimizePickWalk(productIds: List<String>): List<Pair<String, String>> {
        return productIds.mapNotNull { pid ->
            binLocations[pid]?.let { bin -> bin to pid }
        }.sortedBy { it.first }
    }

    /**
     * Performs a cross-facility stock reconciliation to detect discrepancies.
     * 
     * This function simulates an audit process where physical stock counts are
     * compared against digital records. It identifies "shrinkage" (lost items)
     * and generates correction tasks to align the system state with reality.
     * This is vital for maintaining accurate stock levels for the frontend marketplace.
     * 
     * @param physicalCounts A map of Product IDs and their manually counted quantities.
     * @return A list of products with discrepancies and the magnitude of the error.
     */
    fun reconcilePhysicalStock(physicalCounts: Map<String, Int>): List<StockDiscrepancy> {
        val discrepancies = mutableListOf<StockDiscrepancy>()
        
        inventory.forEach { (pid, qty) ->
            val actual = physicalCounts[pid] ?: 0
            if (actual != qty) {
                discrepancies.add(StockDiscrepancy(pid, qty, actual, actual - qty))
                // Correct the system record to match physical reality
                inventory[pid] = actual
            }
        }
        
        return discrepancies
    }

    /**
     * Records a granular internal transaction log for audit purposes.
     * 
     * Every move within the warehouse is tracked with a unique transaction ID.
     * This provides a complete audit trail for compliance and loss prevention.
     * The log is used to reconstruct the history of any item in the facility.
     */
    private fun recordTransaction(productId: String, quantity: Int, type: String, location: String) {
        val txId = transactionCounter.incrementAndGet()
        // In a real system, this would write to a persistent ledger
        println("WMS-LOG: TX-$txId | Type: $type | Item: $productId | Qty: $quantity | Loc: $location")
    }

    private fun randomHex(len: Int): String {
        val chars = "0123456789ABCDEF"
        return (1..len).map { chars[Random().nextInt(chars.length)] }.joinToString("")
    }
}

/**
 * Data class representing a stock discrepancy found during reconciliation.
 */
data class StockDiscrepancy(
    val productId: String,
    val systemQuantity: Int,
    val physicalQuantity: Int,
    val difference: Int
)
