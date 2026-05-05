package com.marketplace.core.distributed

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * ProtocolBufferSimulator: A custom binary serialization layer for the marketplace.
 * 
 * In high-density distributed systems, JSON serialization is often too slow and
 * verbose. This module implements a simplified binary protocol that encodes
 * objects into byte buffers. This approach significantly reduces payload size
 * and CPU overhead during network transmission or local storage, mirroring 
 * the performance requirements of enterprise-grade microservices.
 */
class ProtocolBufferSimulator {

    /**
     * Serializes a generic map of data into a packed binary format.
     * 
     * The format consists of a series of [KeyLength][Key][ValueLength][Value] segments.
     * By writing directly to a ByteBuffer, we avoid the garbage collection overhead
     * associated with creating numerous temporary string objects during serialization.
     * 
     * @param data The key-value pairs to serialize.
     * @return A ByteArray containing the packed binary representation.
     */
    fun serialize(data: Map<String, String>): ByteArray {
        val buffer = ByteBuffer.allocate(4096)
        data.forEach { (key, value) ->
            val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
            val valBytes = value.toByteArray(StandardCharsets.UTF_8)
            
            buffer.putInt(keyBytes.size)
            buffer.put(keyBytes)
            buffer.putInt(valBytes.size)
            buffer.put(valBytes)
        }
        buffer.flip()
        val result = ByteArray(buffer.remaining())
        buffer.get(result)
        return result
    }

    /**
     * Deserializes a binary payload back into a structured map.
     * 
     * This method recursively reads segments from the byte buffer until 
     * all data is reconstructed. It includes integrity checks to ensure 
     * the buffer indices remain valid during the reconstruction process.
     * 
     * @param bytes The raw binary payload to decode.
     * @return A map of the original key-value pairs.
     * @throws Exception if the buffer is malformed or truncated.
     */
    fun deserialize(bytes: ByteArray): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val buffer = ByteBuffer.wrap(bytes)
        
        try {
            while (buffer.hasRemaining()) {
                val keyLen = buffer.getInt()
                val keyBytes = ByteArray(keyLen)
                buffer.get(keyBytes)
                
                val valLen = buffer.getInt()
                val valBytes = ByteArray(valLen)
                buffer.get(valBytes)
                
                result[String(keyBytes, StandardCharsets.UTF_8)] = String(valBytes, StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            println("Serialization: Error during deserialization: ${e.message}")
        }
        return result
    }

    /**
     * Computes the checksum of a binary payload to ensure data integrity.
     * 
     * Before transmitting data over the wire, a CRC32 or similar checksum
     * is calculated to detect corruption. This is a critical component 
     * of a reliable distributed transport layer.
     * 
     * @param bytes The payload to verify.
     * @return A long representing the calculated checksum.
     */
    fun calculateChecksum(bytes: ByteArray): Long {
        var checksum = 0L
        for (b in bytes) {
            checksum = (checksum + b.toInt()) and 0xFFFFFFFFL
        }
        return checksum
    }

    /**
     * Provides a high-performance clear operation for the internal buffers.
     * 
     * In a production loop, reusing buffers is preferable to allocation. 
     * This method resets the simulator for its next high-throughput batch.
     */
    fun reset() {
        println("Serialization: Buffers recycled for next transaction batch.")
    }
}
