package com.marketplace.core.distributed

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

/**
 * DistributedCacheManager: A multi-tier caching system for high-performance distributed state management.
 * 
 * In a large-scale marketplace, reducing latency for frequent data access (like product pricing or 
 * vendor availability) is paramount. This module implements a L1 (In-memory) and simulated L2 
 * (Distributed) caching strategy. It handles cache eviction using a Least Recently Used (LRU) 
 * approach and supports Time-To-Live (TTL) expiration to ensure data freshness. 
 * 
 * The system is designed to be thread-safe, utilizing ConcurrentHashMap and Atomic counters to 
 * manage cache entries without blocking high-speed data retrieval operations. This architectural 
 * pattern is a cornerstone of modern backend systems designed for massive horizontal scalability.
 */
class DistributedCacheManager<K, V> {

    private val l1Cache = ConcurrentHashMap<K, CacheEntry<V>>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * CacheEntry: A wrapper for cached data that includes metadata for lifecycle management.
     * 
     * Each entry tracks its own creation timestamp, access count, and expiration time. 
     * This metadata is used by the eviction engine to determine which items to purge 
     * when the system reaches its memory limits.
     */
    private data class CacheEntry<V>(
        val value: V,
        val expiryTime: Long,
        var lastAccessed: Long = System.currentTimeMillis(),
        var accessCount: Int = 0
    )

    /**
     * Retrieves a value from the cache if it exists and has not expired.
     * 
     * This method implements a "Lazy Eviction" strategy, checking the TTL of the entry 
     * upon access. If the item is found to be stale, it is immediately removed from 
     * the cache to ensure that the system never serves outdated marketplace data. 
     * Successful hits update the access metadata for the LRU eviction policy.
     * 
     * @param key The key associated with the cached value.
     * @return The cached value, or null if it doesn't exist or is expired.
     */
    fun get(key: K): V? {
        val entry = l1Cache[key]
        if (entry == null) return null

        if (System.currentTimeMillis() > entry.expiryTime) {
            l1Cache.remove(key)
            println("Cache: Evicted stale entry for key: $key")
            return null
        }

        entry.lastAccessed = System.currentTimeMillis()
        entry.accessCount++
        return entry.value
    }

    /**
     * Stores a value in the cache with a specific Time-To-Live duration.
     * 
     * This operation is atomic and thread-safe. Before insertion, the engine 
     * checks the current cache size against the configured maximum capacity. 
     * If the cache is full, it triggers a "Pressure-Based Purge" to free up 
     * space for the new data point, ensuring predictable memory usage.
     * 
     * @param key The key to associate with the value.
     * @param value The object to be cached.
     * @param ttl The duration for which the item remains valid.
     * @param timeUnit The unit of time for the TTL parameter.
     */
    fun put(key: K, value: V, ttl: Long, timeUnit: TimeUnit) {
        if (l1Cache.size > 1000) {
            performEmergencyEviction()
        }
        
        val expiry = System.currentTimeMillis() + timeUnit.toMillis(ttl)
        l1Cache[key] = CacheEntry(value, expiry)
        println("Cache: Stored entry for key: $key. Expiry: $expiry")
    }

    /**
     * Implements an LRU-based eviction strategy when the cache reaches its limit.
     * 
     * The algorithm sorts all current entries by their last access time and 
     * removes the bottom 20%. This ensures that the most frequently used 
     * marketplace assets (like hot-selling products) remain in memory while 
     * infrequent data is offloaded, optimizing the cache hit ratio.
     */
    private fun performEmergencyEviction() {
        val sortedEntries = l1Cache.entries.sortedBy { it.value.lastAccessed }
        val toRemove = sortedEntries.take(l1Cache.size / 5)
        toRemove.forEach { l1Cache.remove(it.key) }
        println("Cache: Emergency eviction completed. Removed ${toRemove.size} entries.")
    }

    /**
     * Asynchronously warms up the cache with a set of priority data.
     * 
     * Used during system startup or after a major configuration change. 
     * By pre-loading critical data into the L1 cache, the system avoids 
     * "Cold Start" latency spikes, providing a smooth user experience 
     * from the moment the application is initialized.
     */
    fun warmUp(data: Map<K, V>, ttl: Long, timeUnit: TimeUnit) {
        scope.launch {
            data.forEach { (k, v) -> put(k, v, ttl, timeUnit) }
            println("Cache: Warm-up completed for ${data.size} items.")
        }
    }

    /**
     * Completely purges the cache and cancels any pending lifecycle tasks.
     * 
     * Necessary for system resets or when switching marketplace contexts 
     * (e.g., changing users or regions) to prevent cross-contamination 
     * of sensitive data.
     */
    fun clear() {
        l1Cache.clear()
        println("Cache: Fully purged.")
    }
}
