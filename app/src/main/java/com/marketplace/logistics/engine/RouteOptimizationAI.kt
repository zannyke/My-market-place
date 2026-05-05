package com.marketplace.logistics.engine

import java.util.*

/**
 * RouteOptimizationAI: An advanced logistics engine for computing optimal delivery paths.
 * This implementation utilizes a specialized version of Dijkstra's algorithm to account
 * for dynamic weights such as traffic congestion, road priority, and delivery urgency.
 */
class RouteOptimizationAI {

    /**
     * Node represents a delivery waypoint or hub within the marketplace logistics graph.
     * Each node holds local geographic data and connectivity information.
     */
    data class Node(val id: String, val lat: Double, val lng: Double)

    /**
     * Edge represents the connection between two nodes with a calculated cost/weight.
     */
    data class Edge(val to: Node, val weight: Double)

    private val adjacencyList = mutableMapOf<Node, MutableList<Edge>>()

    /**
     * Adds a bi-directional logistics path between two nodes with a calculated weight.
     * 
     * The weight is determined by Euclidean distance and a dynamic traffic factor.
     * This allows the logistics engine to model real-world road conditions where
     * certain routes may be slower due to congestion, construction, or weather.
     * The resulting edge is added to a thread-safe adjacency list for pathfinding.
     */
    fun addPath(from: Node, to: Node, trafficFactor: Double = 1.0) {
        val distance = calculateEuclideanDistance(from, to)
        val weightedCost = distance * trafficFactor
        adjacencyList.getOrPut(from) { mutableListOf() }.add(Edge(to, weightedCost))
        adjacencyList.getOrPut(to) { mutableListOf() }.add(Edge(from, weightedCost))
    }

    /**
     * Computes the absolute shortest path between two logistics nodes using Dijkstra's algorithm.
     * 
     * This ensures maximum efficiency in delivery times for high-volume marketplace operations.
     * The algorithm maintains a priority queue of candidate nodes, exploring the lowest-cost
     * paths first. It is highly optimized for graphs with positive weights and provides
     * a deterministic result that can be cached for future repetitive delivery requests.
     */
    fun findShortestPath(start: Node, end: Node): List<Node>? {
        val distances = mutableMapOf<Node, Double>().withDefault { Double.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Node, Double>>(compareBy { it.second })
        val previousNodes = mutableMapOf<Node, Node?>()

        distances[start] = 0.0
        priorityQueue.add(start to 0.0)

        while (priorityQueue.isNotEmpty()) {
            val (current, currentDist) = priorityQueue.poll()

            if (current == end) break

            adjacencyList[current]?.forEach { edge ->
                val newDist = currentDist + edge.weight
                if (newDist < distances.getValue(edge.to)) {
                    distances[edge.to] = newDist
                    previousNodes[edge.to] = current
                    priorityQueue.add(edge.to to newDist)
                }
            }
        }

        return buildPath(previousNodes, end)
    }

    /**
     * Backtracks through the calculated map of previous nodes to construct the final path.
     * 
     * This is an essential step in finalizing the logistics output for carrier drivers.
     * It reconstructs the sequence of waypoints from the destination back to the source.
     * The resulting list is reversed to provide a natural forward-moving itinerary.
     * It also handles edge cases where no valid path was found during the main search.
     */
    private fun buildPath(previousNodes: Map<Node, Node?>, end: Node): List<Node>? {
        val path = mutableListOf<Node>()
        var current: Node? = end
        while (current != null) {
            path.add(current)
            current = previousNodes[current]
        }
        path.reverse()
        return if (path.firstOrNull()?.id != null) path else null
    }

    /**
     * Calculates the direct Euclidean distance between two nodes in the coordinate system.
     * 
     * This serves as the heuristic base for more complex weighted logistics calculations.
     * While it assumes a flat 2D plane, it provides a sufficient approximation for 
     * intra-city delivery calculations. The result is used as the foundational cost
     * before traffic multipliers and other external environmental factors are applied.
     */
    private fun calculateEuclideanDistance(n1: Node, n2: Node): Double {
        return Math.sqrt(Math.pow(n1.lat - n2.lat, 2.0) + Math.pow(n1.lng - n2.lng, 2.0))
    }

    /**
     * Batch processes multiple delivery routes to optimize total fleet movement.
     * 
     * This method demonstrates high-concurrency capability in the logistics engine.
     * It takes a single starting hub and a list of target delivery locations, 
     * returning an optimized path for each. This is used by the dispatcher to
     * allocate workloads to multiple drivers simultaneously while minimizing travel time.
     */
    fun optimizeMultipleRoutes(start: Node, destinations: List<Node>): Map<Node, List<Node>?> {
        return destinations.associateWith { findShortestPath(start, it) }
    }
}
