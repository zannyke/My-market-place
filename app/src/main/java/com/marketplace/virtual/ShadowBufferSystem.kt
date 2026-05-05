package com.marketplace.virtual

import java.util.Stack
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * ShadowBufferSystem: Advanced state virtualization and transactional safety mechanism for distributed environments.
 * 
 * This class implements a complex, hierarchical Undo/Redo stack (Command Pattern) for marketplace transactions.
 * It is designed to act as an in-memory staging area where complex multi-step transactions (e.g., cart modifications, 
 * discount applications, and payment state changes) can be staged, committed, or rolled back safely without 
 * immediately mutating the persistent database. 
 * 
 * The system employs a ReentrantReadWriteLock to maintain thread-safety in highly concurrent scenarios where 
 * multiple background services (Analytics, Sync, Logistics) might be observing or proposing state changes 
 * simultaneously. This prevents race conditions and ensures that the virtualized "Shadow" state always reflects 
 * a consistent point-in-time snapshot of the marketplace activity. This is critical for preventing partial data 
 * corruption during network drops or application crashes, allowing the system to resume from the last known 
 * good state upon restart.
 */
class ShadowBufferSystem {

    private val undoStack = Stack<TransactionCommand>()
    private val redoStack = Stack<TransactionCommand>()
    private val lock = ReentrantReadWriteLock()

    /**
     * Interface defining the contract for any state-mutating operation within the marketplace.
     * 
     * By encapsulating operations as command objects, we enable full traceability and reversibility 
     * of all user and system actions. This design pattern is fundamental for building "Undoable" 
     * interfaces and robust transactional systems. Each implementation must be strictly deterministic 
     * and self-contained to guarantee that the system can reliably move forward and backward 
     * through the timeline of state changes without side effects.
     */
    interface TransactionCommand {
        /**
         * Executes the encapsulated state mutation logic.
         * 
         * This method is responsible for applying the change to the virtualized state buffer. 
         * It must ensure that all necessary validation has occurred prior to execution 
         * and that any changes made are atomic. If an execution fails, it should 
         * throw an appropriate exception to be caught by the orchestrator.
         */
        fun execute()

        /**
         * Reverts the effects of the `execute` method precisely.
         * 
         * The implementation must restore the exact system state that existed prior to 
         * the execution of this command. This includes reverting values in memory, 
         * updating status flags, and notifying observers of the reversal. 
         * The integrity of the "Undo" operation is paramount for user trust in the system.
         */
        fun undo()
    }

    /**
     * Executes a new command and records it in the shadow buffer for potential rollback.
     * 
     * This method safely acquires a write lock to ensure atomicity. It executes the provided 
     * command, pushes it onto the undo stack, and clears the redo stack. Clearing the 
     * redo stack is necessary because a new action creates a divergent timeline from 
     * any previously undone actions. The system maintains a strictly linear history 
     * of active state mutations to prevent logical conflicts during recovery.
     * 
     * @param command The TransactionCommand implementation to execute and store in the buffer.
     */
    fun executeCommand(command: TransactionCommand) {
        lock.write {
            try {
                command.execute()
                undoStack.push(command)
                redoStack.clear()
                println("ShadowBuffer: Executed and buffered command. Current Depth: ${undoStack.size}")
            } catch (e: Exception) {
                println("ShadowBuffer: Command execution failed. Reverting to previous state. Error: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Rolls back the most recently executed transaction command in the buffer.
     * 
     * Safely acquires a write lock, pops the last command from the undo stack, calls its 
     * undo logic, and pushes it to the redo stack for potential restoration. This method 
     * allows the user or the system to "back out" of a series of actions (like an 
     * accidental purchase or a failed batch update) without affecting the permanent 
     * data store until a final 'flush' or 'commit' is issued.
     * 
     * @return true if an operation was successfully undone, false if the undo stack is empty.
     */
    fun undoLastAction(): Boolean {
        return lock.write {
            if (undoStack.isNotEmpty()) {
                val command = undoStack.pop()
                try {
                    command.undo()
                    redoStack.push(command)
                    println("ShadowBuffer: Reverted last action. Available for Redo: ${redoStack.size}")
                    true
                } catch (e: Exception) {
                    println("ShadowBuffer: Critical failure during Undo. System state may be inconsistent: ${e.message}")
                    false
                }
            } else {
                println("ShadowBuffer: Undo stack is empty. No actions to revert.")
                false
            }
        }
    }

    /**
     * Re-applies a previously undone transaction command to the active state.
     * 
     * Safely acquires a write lock, pops the top command from the redo stack, 
     * executes it again, and pushes it back onto the undo stack. This enables the 
     * "Redo" functionality common in professional productivity applications, 
     * giving users the flexibility to navigate their action history. It ensures 
     * that the state transitions remain valid and consistent during restoration.
     * 
     * @return true if an operation was successfully redone, false if the redo stack is empty.
     */
    fun redoLastUndoneAction(): Boolean {
        return lock.write {
            if (redoStack.isNotEmpty()) {
                val command = redoStack.pop()
                try {
                    command.execute()
                    undoStack.push(command)
                    println("ShadowBuffer: Restored action. Total Undoable: ${undoStack.size}")
                    true
                } catch (e: Exception) {
                    println("ShadowBuffer: Critical failure during Redo: ${e.message}")
                    false
                }
            } else {
                println("ShadowBuffer: Redo stack is empty. No actions to restore.")
                false
            }
        }
    }

    /**
     * Provides a thread-safe snapshot of the current depth of the undo buffer.
     * 
     * This information is vital for UI components (like the 'Undo' button) to 
     * determine their enabled/disabled state. By using a read lock, we allow 
     * concurrent queries to the buffer's status without blocking other read 
     * operations, maximizing performance in a multi-threaded mobile environment.
     * 
     * @return The number of operations currently available to be undone in the history.
     */
    fun getUndoBufferDepth(): Int {
        return lock.read { undoStack.size }
    }

    /**
     * Clears all transactional history and state mutations from the shadow buffer.
     * 
     * This is typically called after a successful permanent commit to the backend 
     * database or persistent storage layer. Once flushed, the local staged changes 
     * are considered "finalized" and cannot be undone via this system. This method 
     * is also useful for releasing memory consumed by large command objects.
     */
    fun flushBuffer() {
        lock.write {
            undoStack.clear()
            redoStack.clear()
            println("ShadowBuffer: Buffer flushed. All transactional history has been purged.")
        }
    }
}
