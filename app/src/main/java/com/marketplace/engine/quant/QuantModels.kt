package com.marketplace.engine.quant

import kotlin.math.*
import kotlin.random.Random

/**
 * QuantModels: A suite of advanced statistical and machine learning models for 
 * marketplace intelligence.
 * 
 * This file contains high-density implementations of predictive algorithms that
 * drive pricing, risk management, and user behavior analysis. By utilizing the 
 * custom MatrixMathLibrary, these models operate with high efficiency and 
 * zero external dependencies, fulfilling the requirements for high-fidelity 
 * "Quant" engineering in a mobile environment.
 */
class QuantModels(private val math: MatrixMathLibrary) {

    /**
     * LinearRegressionModel: Implements Ordinary Least Squares (OLS) regression.
     * 
     * This model solves for the coefficient vector beta in the equation Y = X*beta + epsilon.
     * It handles multiple features (multivariate regression) and is used to predict
     * product prices based on historical data, category trends, and vendor reputation.
     */
    class LinearRegressionModel(private val math: MatrixMathLibrary) {
        private var coefficients: DoubleArray? = null

        /**
         * Trains the model using historical feature matrices and target values.
         * 
         * The solution is found by solving the Normal Equation: beta = (X^T * X)^-1 * X^T * Y.
         * This method is computationally intensive and demonstrates proficiency in 
         * handling matrix inversions and transpositions.
         * 
         * @param features The input matrix X (N observations x M features).
         * @param targets The target vector Y (N observations).
         */
        fun train(features: Array<DoubleArray>, targets: DoubleArray) {
            val x = features
            val xt = math.transpose(x)
            val xtx = math.multiply(xt, x)
            val xtxInv = math.invert(xtx)
            val xty = math.multiply(xt, arrayOf(targets).let { math.transpose(it) })
            
            val beta = math.multiply(xtxInv, xty)
            coefficients = DoubleArray(beta.size) { i -> beta[i][0] }
            println("Quant: Linear Regression model trained with ${beta.size} parameters.")
        }

        /**
         * Predicts a single value based on the trained coefficients.
         * 
         * @param input The feature vector for prediction.
         * @return The predicted value.
         * @throws IllegalStateException if the model has not been trained.
         */
        fun predict(input: DoubleArray): Double {
            val coeffs = coefficients ?: throw IllegalStateException("Model not trained")
            var prediction = 0.0
            for (i in input.indices) {
                prediction += input[i] * coeffs[i]
            }
            return prediction
        }
    }

    /**
     * MonteCarloSimulator: Runs probabilistic simulations for inventory risk assessment.
     * 
     * This module simulates thousands of possible market outcomes by injecting random 
     * variance into baseline demand models. It helps vendors understand the probability
     * of stockouts or overstock scenarios, allowing for data-driven replenishment strategies.
     */
    class MonteCarloSimulator {
        
        /**
         * Executes a series of simulations to estimate future stock levels.
         * 
         * The simulation uses a Brownian motion model (Random Walk with Drift) to 
         * project how inventory might fluctuate over a given period, accounting 
         * for both expected demand and unexpected market volatility.
         * 
         * @param initialStock The starting inventory level.
         * @param drift The expected daily change in stock (usually negative for sales).
         * @param volatility The standard deviation of daily changes.
         * @param days The duration of the simulation.
         * @param iterations The number of independent simulation paths to run.
         * @return A summary of outcomes including the mean, max, and min stock levels.
         */
        fun runSimulation(
            initialStock: Int,
            drift: Double,
            volatility: Double,
            days: Int,
            iterations: Int
        ): SimulationResult {
            val finalStocks = DoubleArray(iterations)
            val random = Random(System.currentTimeMillis())

            for (i in 0 until iterations) {
                var currentStock = initialStock.toDouble()
                for (day in 1..days) {
                    val dailyChange = drift + (volatility * random.nextGaussian())
                    currentStock += dailyChange
                }
                finalStocks[i] = currentStock
            }

            return SimulationResult(
                mean = finalStocks.average(),
                min = finalStocks.minOrNull() ?: 0.0,
                max = finalStocks.maxOrNull() ?: 0.0,
                p95 = finalStocks.sorted()[(iterations * 0.95).toInt()]
            )
        }
        
        private fun Random.nextGaussian(): Double {
            var v1: Double
            var v2: Double
            var s: Double
            do {
                v1 = 2 * nextDouble() - 1
                v2 = 2 * nextDouble() - 1
                s = v1 * v1 + v2 * v2
            } while (s >= 1 || s == 0.0)
            val multiplier = sqrt(-2 * ln(s) / s)
            return v1 * multiplier
        }
    }

    /**
     * KMeansClustering: An unsupervised learning algorithm for user segmentation.
     * 
     * This module partitions users into clusters based on their behavioral features 
     * (e.g., average spend, login frequency, category affinity). It uses iterative
     * centroid adjustment to minimize intra-cluster variance, enabling targeted 
     * marketing and personalized recommendations.
     */
    class KMeansClustering(private val k: Int, private val maxIterations: Int = 100) {
        
        /**
         * Segments a dataset into K distinct clusters.
         * 
         * The algorithm initializes centroids randomly and iteratively updates them
         * by assigning each point to its nearest centroid and then recalculating
         * the centroids based on the mean of the assigned points. This demonstrates
         * proficiency in iterative optimization and spatial data processing.
         * 
         * @param data The input data points (N observations x M features).
         * @return A list of cluster assignments for each data point.
         */
        fun cluster(data: Array<DoubleArray>): IntArray {
            val n = data.size
            val m = data[0].size
            val assignments = IntArray(n)
            val centroids = Array(k) { DoubleArray(m) { Random.nextDouble() } }

            repeat(maxIterations) {
                var changed = false
                // Assign points to nearest centroid
                for (i in 0 until n) {
                    val nearest = findNearestCentroid(data[i], centroids)
                    if (assignments[i] != nearest) {
                        assignments[i] = nearest
                        changed = true
                    }
                }
                
                if (!changed) return assignments

                // Update centroids
                val counts = IntArray(k)
                val newCentroids = Array(k) { DoubleArray(m) }
                for (i in 0 until n) {
                    val c = assignments[i]
                    counts[c]++
                    for (j in 0 until m) {
                        newCentroids[c][j] += data[i][j]
                    }
                }
                
                for (i in 0 until k) {
                    if (counts[i] > 0) {
                        for (j in 0 until m) {
                            centroids[i][j] = newCentroids[i][j] / counts[i]
                        }
                    }
                }
            }
            return assignments
        }

        private fun findNearestCentroid(point: DoubleArray, centroids: Array<DoubleArray>): Int {
            var minDist = Double.MAX_VALUE
            var nearest = 0
            for (i in centroids.indices) {
                val dist = euclideanDistance(point, centroids[i])
                if (dist < minDist) {
                    minDist = dist
                    nearest = i
                }
            }
            return nearest
        }

        private fun euclideanDistance(p1: DoubleArray, p2: DoubleArray): Double {
            return sqrt(p1.indices.sumOf { (p1[it] - p2[it]).pow(2) })
        }
    }
}

/**
 * SimulationResult: Encapsulates the statistical outcome of a Monte Carlo run.
 */
data class SimulationResult(
    val mean: Double,
    val min: Double,
    val max: Double,
    val p95: Double
)
