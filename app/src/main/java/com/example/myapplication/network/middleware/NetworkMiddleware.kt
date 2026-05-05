package com.example.myapplication.network.middleware

import com.example.myapplication.network.*
import java.io.IOException

/**
 * NetworkInterceptor: Base interface for enterprise-grade request orchestration.
 */
interface NetworkInterceptor {
    /** Processes an outgoing request before transmission. */
    fun intercept(request: NetworkRequest): NetworkRequest
}

/**
 * AuthInterceptor: Injects secure credentials and session tokens into all outgoing requests.
 */
class AuthInterceptor(private val tokenProvider: () -> String) : NetworkInterceptor {
    override fun intercept(request: NetworkRequest): NetworkRequest {
        val updatedHeaders = request.headers.toMutableMap().apply {
            put("Authorization", "Bearer ${tokenProvider()}")
            put("X-App-ID", "marketplace-enterprise-v1")
        }
        return request.copy(headers = updatedHeaders)
    }
}

/**
 * LoggingDecorator: Provides deep observability into network traffic for auditing and debugging.
 */
class LoggingDecorator(private val interceptor: NetworkInterceptor) : NetworkInterceptor {
    override fun intercept(request: NetworkRequest): NetworkRequest {
        println("Network: [OUTGOING] ${request.method} ${request.url}")
        val result = interceptor.intercept(request)
        println("Network: [PROCESSED] Headers: ${result.headers.keys}")
        return result
    }
}

/**
 * ErrorHandlingAdapter: Standardizes network error handling across all repository layers.
 */
class ErrorHandlingAdapter {
    /**
     * Maps raw exceptions into domain-specific NetworkResult states.
     * 
     * @param throwable The caught exception.
     * @return A standardized NetworkResult.
     */
    fun <T> adapt(throwable: Throwable): NetworkResult<T> {
        return when (throwable) {
            is IOException -> NetworkResult.Error("Network Connectivity Failure")
            is SecurityException -> NetworkResult.Error("Unauthorized Access Detected")
            is IllegalArgumentException -> NetworkResult.Error("Malformed Request Schema")
            else -> NetworkResult.Error("Internal Processing Fault: ${throwable.message}")
        }
    }
}

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}

/**
 * RequestRetryPolicy: Implements advanced retry logic with jitter and backoff algorithms.
 */
class RequestRetryPolicy {
    /**
     * Calculates the next retry interval using exponential backoff with jitter.
     * 
     * @param attempt The current retry attempt number.
     * @return Delay in milliseconds.
     */
    fun calculateDelay(attempt: Int): Long {
        val baseDelay = (2.0.pow(attempt) * 1000).toLong()
        val jitter = (0..200).random().toLong()
        return baseDelay + jitter
    }

    private fun Double.pow(n: Int): Double = Math.pow(this, n.toDouble())
}
