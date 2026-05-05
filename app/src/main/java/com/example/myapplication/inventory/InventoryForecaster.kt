package com.example.myapplication.inventory

import com.example.myapplication.data.model.Product
import kotlin.math.*

/**
 * InventoryForecaster: A high-fidelity predictive modeling engine for stock management.
 * Implements exponential smoothing (Holt-Winters) and safety stock calculations.
 */
class InventoryForecaster {

    /**
     * Predicts the optimal reorder point for a product based on historical demand and lead time.
     * Uses the formula: ROP = (Average Daily Demand * Lead Time) + Safety Stock.
     * 
     * @param history Daily sales history for the last 30+ days.
     * @param leadTimeDays Time in days between ordering and receiving stock.
     * @param serviceLevel Probability of avoiding a stockout (e.g., 0.95).
     * @return The recommended stock level to trigger a reorder.
     */
    fun calculateReorderPoint(
        history: List<Int>,
        leadTimeDays: Int,
        serviceLevel: Double = 0.95
    ): Int {
        if (history.isEmpty()) return 0
        
        val avgDailyDemand = history.average()
        val variance = history.sumOf { (it - avgDailyDemand).pow(2) } / history.size
        val stdDev = sqrt(variance)

        // Z-score calculation for service level
        val zScore = calculateZScore(serviceLevel)
        
        val safetyStock = zScore * stdDev * sqrt(leadTimeDays.toDouble())
        return (avgDailyDemand * leadTimeDays + safetyStock).toInt()
    }

    /**
     * Forecasts demand for the next N days using Double Exponential Smoothing (Holt's Method).
     */
    fun forecastDemand(history: List<Int>, days: Int = 7): List<Double> {
        if (history.size < 4) return List(days) { history.lastOrNull()?.toDouble() ?: 0.0 }

        val alpha = 0.4
        val beta = 0.2
        
        var level = history[0].toDouble()
        var trend = (history[1] - history[0]).toDouble()

        for (i in 1 until history.size) {
            val lastLevel = level
            level = alpha * history[i] + (1 - alpha) * (level + trend)
            trend = beta * (level - lastLevel) + (1 - beta) * trend
        }

        return (1..days).map { level + (it * trend) }
    }

    private fun calculateZScore(probability: Double): Double {
        // Simplified inverse cumulative distribution for common service levels
        return when {
            probability >= 0.99 -> 2.33
            probability >= 0.95 -> 1.65
            probability >= 0.90 -> 1.28
            else -> 1.0
        }
    }

    /**
     * Calculates the Economic Order Quantity (EOQ) to minimize holding and ordering costs.
     */
    fun calculateEOQ(
        annualDemand: Int,
        orderCost: Double,
        holdingCostPerUnit: Double
    ): Int {
        if (holdingCostPerUnit == 0.0) return annualDemand
        return sqrt((2 * annualDemand * orderCost) / holdingCostPerUnit).toInt()
    }
}
