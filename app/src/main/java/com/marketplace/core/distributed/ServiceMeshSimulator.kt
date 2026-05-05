package com.marketplace.core.distributed

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * ServiceMeshSimulator: Orchestrates distributed service communication and reliability.
 * 
 * In a large-scale marketplace, services must communicate across unreliable networks.
 * This module simulates a service mesh by providing circuit breakers, retry logic,
 * and service discovery. It ensures that transient failures in one part of the 
 * system do not cascade into global outages, a critical requirement for high-availability
 * enterprise applications.
 */
class ServiceMeshSimulator(private val scope: CoroutineScope) {

    private val activeCircuits = mutableMapOf<String, CircuitBreaker>()

    /**
     * Executes a network call with an integrated circuit breaker and retry policy.
     * 
     * This method wraps a suspending block of code (the network request) and monitors
     * its success rate. If the failure threshold is reached, the circuit "trips" and
     * subsequent calls are immediately rejected, allowing the remote service time
     * to recover. It also applies an exponential backoff strategy for retries.
     * 
     * @param serviceName The unique identifier for the remote service.
     * @param request A suspending function that performs the actual network work.
     * @return The result of the request or an error message.
     */
    suspend fun <T> executeRequest(
        serviceName: String,
        retryCount: Int = 3,
        request: suspend () -> T
    ): Result<T> {
        val circuit = activeCircuits.getOrPut(serviceName) { CircuitBreaker(serviceName) }

        if (circuit.isOpen()) {
            return Result.failure(Exception("Circuit breaker for $serviceName is OPEN. Request rejected."))
        }

        var currentAttempt = 0
        while (currentAttempt < retryCount) {
            try {
                val result = request()
                circuit.recordSuccess()
                return Result.success(result)
            } catch (e: Exception) {
                currentAttempt++
                circuit.recordFailure()
                
                if (currentAttempt >= retryCount) {
                    return Result.failure(e)
                }

                // Exponential Backoff with Jitter
                val delayTime = (2.0.pow(currentAttempt).toLong() * 100) + Random.nextLong(0, 100)
                println("ServiceMesh: Request to $serviceName failed. Retrying in $delayTime ms...")
                delay(delayTime)
            }
        }
        return Result.failure(Exception("Max retries reached for $serviceName"))
    }

    /**
     * Internal Circuit Breaker state management.
     * 
     * The circuit breaker transitions between CLOSED, OPEN, and HALF-OPEN states
     * based on the failure rate of the associated service. This pattern is
     * fundamental to building resilient distributed systems.
     */
    private class CircuitBreaker(val name: String) {
        private val failureThreshold = 5
        private val resetTimeoutMs = 5000L
        private val failureCount = AtomicInteger(0)
        private var lastFailureTime = 0L
        private var state = State.CLOSED

        enum class State { CLOSED, OPEN, HALF_OPEN }

        /**
         * Determines if the circuit is currently blocking requests.
         * 
         * @return True if the circuit is in the OPEN state and the timeout hasn't passed.
         */
        fun isOpen(): Boolean {
            if (state == State.OPEN && System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
                state = State.HALF_OPEN
                println("ServiceMesh: Circuit $name transitioning to HALF_OPEN")
            }
            return state == State.OPEN
        }

        /**
         * Records a failed request and trips the circuit if the threshold is reached.
         */
        fun recordFailure() {
            val count = failureCount.incrementAndGet()
            if (count >= failureThreshold) {
                state = State.OPEN
                lastFailureTime = System.currentTimeMillis()
                println("ServiceMesh: Circuit $name TRIPPED to OPEN")
            }
        }

        /**
         * Records a successful request and resets the circuit back to CLOSED.
         */
        fun recordSuccess() {
            failureCount.set(0)
            state = State.CLOSED
            println("ServiceMesh: Circuit $name reset to CLOSED")
        }
    }

    private fun Double.pow(n: Int): Double = Math.pow(this, n.toDouble())
}
