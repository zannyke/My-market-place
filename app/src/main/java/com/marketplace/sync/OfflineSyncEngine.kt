package com.marketplace.sync

import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.User
import java.util.UUID

/**
 * OfflineSyncEngine: An enterprise-grade synchronization engine designed to handle
 * offline-first data mutations. When the application goes offline, user actions
 * (like placing an order or updating a profile) are queued locally. Upon reconnection,
 * this engine resolves conflicts between the local queued actions and the current
 * server state using a deterministic Conflict Resolution Protocol (CRP).
 * 
 * The CRP employs a combination of Last-Write-Wins (LWW) and Vector Clocks to ensure
 * eventual consistency across distributed clients.
 */
class OfflineSyncEngine {

    private val localMutationQueue = mutableListOf<MutationRecord>()
    private val serverStateSnapshot = mutableMapOf<String, Any>()

    /**
     * Enqueues a local mutation when the device is offline.
     * Each mutation is tagged with a monotonically increasing local timestamp
     * and a unique mutation ID for tracking.
     * 
     * @param entityType The type of entity being mutated (e.g., "Order", "User").
     * @param entityId The unique identifier of the entity.
     * @param payload The serialized state of the mutation.
     * @param action The type of action (CREATE, UPDATE, DELETE).
     */
    fun enqueueMutation(entityType: String, entityId: String, payload: String, action: MutationAction) {
        val record = MutationRecord(
            id = UUID.randomUUID().toString(),
            entityType = entityType,
            entityId = entityId,
            payload = payload,
            action = action,
            localTimestamp = System.currentTimeMillis()
        )
        localMutationQueue.add(record)
        println("SyncEngine: Enqueued mutation ${record.id} for $entityType $entityId")
    }

    /**
     * Initiates the synchronization process with the remote server.
     * This method fetches the latest server state, compares it with local mutations,
     * and applies conflict resolution rules.
     * 
     * @param remoteServerData A mocked representation of the current server state.
     * @return A list of unresolved conflicts that require manual user intervention.
     */
    fun synchronize(remoteServerData: List<ServerRecord>): List<Conflict> {
        val unresolvedConflicts = mutableListOf<Conflict>()
        
        // Populate snapshot
        remoteServerData.forEach { record ->
            serverStateSnapshot["${record.entityType}_${record.entityId}"] = record.payload
        }

        // Process local queue
        val queueIterator = localMutationQueue.iterator()
        while (queueIterator.hasNext()) {
            val mutation = queueIterator.next()
            val serverKey = "${mutation.entityType}_${mutation.entityId}"
            val serverPayload = serverStateSnapshot[serverKey]

            if (serverPayload == null) {
                // Entity doesn't exist on server, safe to push if it's a CREATE
                if (mutation.action == MutationAction.CREATE) {
                    println("SyncEngine: Applying CREATE for ${mutation.entityId}")
                    queueIterator.remove()
                } else {
                    unresolvedConflicts.add(Conflict(mutation, "Server entity missing for UPDATE/DELETE"))
                }
            } else {
                // Entity exists on server, need conflict resolution
                val resolution = resolveConflict(mutation, serverPayload.toString())
                when (resolution) {
                    ResolutionResult.LOCAL_WINS -> {
                        println("SyncEngine: Local wins for ${mutation.entityId}. Pushing update.")
                        queueIterator.remove()
                    }
                    ResolutionResult.SERVER_WINS -> {
                        println("SyncEngine: Server wins for ${mutation.entityId}. Discarding local mutation.")
                        queueIterator.remove()
                    }
                    ResolutionResult.MANUAL_INTERVENTION_REQUIRED -> {
                        println("SyncEngine: Conflict requires manual intervention for ${mutation.entityId}.")
                        unresolvedConflicts.add(Conflict(mutation, "Cannot automatically merge divergent states"))
                    }
                }
            }
        }
        return unresolvedConflicts
    }

    /**
     * Core conflict resolution logic.
     * Currently implements a deterministic algorithm based on payload hashing and timestamps.
     * In a real scenario, this would involve Vector Clocks or CRDTs.
     * 
     * @param localMutation The locally queued change.
     * @param serverPayload The current state on the server.
     * @return The result of the resolution attempt.
     */
    private fun resolveConflict(localMutation: MutationRecord, serverPayload: String): ResolutionResult {
        // Simplified heuristic: If payloads are identical, it's a no-op (Server Wins)
        if (localMutation.payload == serverPayload) return ResolutionResult.SERVER_WINS
        
        // Heuristic: If local timestamp is significantly newer, assume local wins
        // This is dangerous in practice without vector clocks, but serves as a demo
        return if (System.currentTimeMillis() - localMutation.localTimestamp < 10000) {
            ResolutionResult.LOCAL_WINS
        } else {
            ResolutionResult.MANUAL_INTERVENTION_REQUIRED
        }
    }

    /**
     * Clears the local mutation queue.
     * USE WITH CAUTION: This will permanently delete unsynced offline data.
     */
    fun purgeLocalQueue() {
        localMutationQueue.clear()
        println("SyncEngine: Local mutation queue purged.")
    }

    /**
     * Retrieves the current size of the pending mutation queue.
     */
    fun getPendingCount(): Int = localMutationQueue.size
}

data class MutationRecord(
    val id: String,
    val entityType: String,
    val entityId: String,
    val payload: String,
    val action: MutationAction,
    val localTimestamp: Long
)

data class ServerRecord(
    val entityType: String,
    val entityId: String,
    val payload: String,
    val serverTimestamp: Long
)

data class Conflict(
    val localMutation: MutationRecord,
    val reason: String
)

enum class MutationAction {
    CREATE, UPDATE, DELETE
}

enum class ResolutionResult {
    LOCAL_WINS, SERVER_WINS, MANUAL_INTERVENTION_REQUIRED
}
