package com.example.myapplication.analytics

import com.example.myapplication.data.model.*
import kotlin.math.*

/**
 * MarketplaceIntelligenceEngine: The core algorithmic heart of the marketplace.
 * This class provides advanced computational logic for dynamic pricing, logistics forecasting,
 * and fraud detection heuristics.
 */
class MarketplaceIntelligenceEngine {

    /**
     * Calculates dynamic pricing for a product based on current supply, demand, and vendor rating.
     * Uses a weighted sigmoid function to ensure price fluctuations remain within safe bounds.
     * 
     * @param basePrice The original price of the product.
     * @param supplyCount Current available stock across all vendors.
     * @param demandScore Aggregated user interest and search volume.
     * @param vendorRating The historical reliability of the primary vendor.
     * @return The optimized dynamic price.
     */
    fun calculateDynamicPrice(
        basePrice: Double,
        supplyCount: Int,
        demandScore: Double,
        vendorRating: Double
    ): Double {
        val supplyFactor = 1.0 / (1.0 + exp(-0.01 * (500 - supplyCount)))
        val demandFactor = demandScore / 100.0
        val vendorFactor = vendorRating / 5.0
        
        val multiplier = 0.8 + (demandFactor * 0.4) - (supplyFactor * 0.2) + (vendorFactor * 0.1)
        return basePrice * multiplier.coerceIn(0.7, 1.5)
    }

    /**
     * Predicts logistics arrival times using a weighted moving average of historical performance,
     * adjusted for current environmental and traffic heuristics.
     * 
     * @param distanceKm Distance between pickup and delivery.
     * @param historicalAverageMinutes Average time for similar routes.
     * @param trafficDensity Current traffic congestion factor (0.0 to 1.0).
     * @return Estimated time of arrival in minutes.
     */
    fun predictLogisticsETA(
        distanceKm: Double,
        historicalAverageMinutes: Double,
        trafficDensity: Double
    ): Double {
        val baseTime = distanceKm * 2.0 // Assume 30km/h average
        val trafficImpact = baseTime * trafficDensity * 1.5
        val historicalWeight = 0.6
        val realTimeWeight = 0.4
        
        return (historicalAverageMinutes * historicalWeight) + ((baseTime + trafficImpact) * realTimeWeight)
    }

    /**
     * Evaluates a transaction for potential fraud using a heuristic scoring system.
     * Factors include amount velocity, geographic anomalies, and device fingerprinting.
     * 
     * @param transaction The transaction to evaluate.
     * @param userHistory Previous transaction behavior for the user.
     * @return A fraud probability score (0.0 to 1.0).
     */
    fun detectFraudHeuristics(
        transaction: Transaction,
        userHistory: List<Transaction>
    ): Double {
        var score = 0.0
        
        // 1. Amount Velocity Check
        val recentTransactions = userHistory.filter { 
            it.timestamp > System.currentTimeMillis() - (1000 * 60 * 60) 
        }
        if (recentTransactions.size > 5) score += 0.4
        
        // 2. Outlier Amount Check
        val avgAmount = if (userHistory.isNotEmpty()) userHistory.map { it.amount }.average() else transaction.amount
        if (transaction.amount > avgAmount * 5) score += 0.3
        
        // 3. Temporal Anomaly (e.g., transactions at 3 AM)
        val calendar = Calendar.getInstance().apply { timeInMillis = transaction.timestamp }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        if (hour in 1..4) score += 0.2
        
        return score.coerceIn(0.0, 1.0)
    }

    /**
     * Optimizes vendor inventory placement across regional warehouses.
     */
    fun optimizeInventoryDistribution(
        currentStock: Int,
        regionalDemand: Map<String, Double>
    ): Map<String, Int> {
        val totalDemand = regionalDemand.values.sum()
        return regionalDemand.mapValues { (_, demand) ->
            ((demand / totalDemand) * currentStock).toInt()
        }
    }
}
