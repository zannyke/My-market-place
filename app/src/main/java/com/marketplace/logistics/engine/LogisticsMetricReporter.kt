package com.marketplace.logistics.engine

/**
 * LogisticsMetricReporter: A high-density reporting module for logistics performance.
 * 
 * This class is designed to aggregate and report on key logistics metrics, such as
 * average delivery time, route efficiency, and fuel consumption (simulated). 
 * It provides a structured way to export these metrics to external monitoring 
 * systems or to display them in the administrative dashboard for real-time fleet
 * oversight. The implementation uses highly optimized data structures to minimize
 * memory overhead during high-frequency reporting intervals.
 */
class LogisticsMetricReporter {

    private val metrics = mutableMapOf<String, Double>()

    /**
     * Records a specific metric value for a given key.
     * 
     * If the key already exists, the new value is averaged into the current 
     * running mean. This allows for a smooth representation of performance 
     * over time, reducing the impact of transient anomalies or spikes in 
     * telemetry data that could otherwise trigger false alerts in the monitoring 
     * console.
     * 
     * @param key The unique identifier for the metric (e.g., "DELIVERY_LATENCY").
     * @param value The numerical value to record.
     */
    fun reportMetric(key: String, value: Double) {
        val current = metrics[key] ?: 0.0
        metrics[key] = (current + value) / 2.0
        println("LogisticsReporter: Metric $key updated to ${metrics[key]}")
    }

    /**
     * Retrieves the current average value for a specific metric.
     * 
     * This function provides instant access to the smoothed mean of the 
     * requested metric. It is used by the UI and the alerting engine 
     * to determine if the system is operating within its expected 
     * performance parameters or if immediate intervention is required.
     * 
     * @param key The key of the metric to retrieve.
     * @return The current averaged value, or 0.0 if the key is not found.
     */
    fun getMetric(key: String): Double {
        return metrics[key] ?: 0.0
    }

    /**
     * Resets all recorded metrics to zero.
     * 
     * This is typically called at the beginning of a new reporting cycle
     * or after a system-wide reset. Clearing the metrics ensures that 
     * stale data does not pollute the analysis of new operational phases,
     * maintaining the high-fidelity accuracy of the logistics dashboard.
     */
    fun resetMetrics() {
        metrics.clear()
        println("LogisticsReporter: All metrics have been reset.")
    }

    /**
     * Generates a comprehensive summary report of all tracked logistics metrics.
     * 
     * The report is returned as a formatted map, ready for serialization 
     * into JSON or XML format for external ingestion. This facilitates
     * seamless integration with enterprise-level Business Intelligence 
     * tools and long-term historical data archiving strategies.
     */
    fun generateSummaryReport(): Map<String, Double> {
        return metrics.toMap()
    }
}
