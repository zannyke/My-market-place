package com.example.myapplication.search

import com.example.myapplication.data.model.Product
import kotlin.math.*

/**
 * MarketplaceSearchEngine: A high-complexity search and ranking engine for the marketplace.
 * Implements TF-IDF style weighting, category boosting, and fuzzy matching heuristics.
 */
class MarketplaceSearchEngine {

    /**
     * Executes a complex search query across the product catalog.
     * Uses a multi-factor ranking algorithm to sort results by relevance.
     * 
     * @param query The user's search string.
     * @param products The full catalog of products.
     * @param categoryBoost Optional category IDs to prioritize.
     * @return A list of products ranked by relevance.
     */
    fun search(
        query: String,
        products: List<Product>,
        categoryBoost: Set<String> = emptySet()
    ): List<Product> {
        if (query.isEmpty()) return products

        val tokens = query.lowercase().split(" ").filter { it.length > 2 }
        
        return products.map { product ->
            val score = calculateRelevanceScore(tokens, product, categoryBoost)
            product to score
        }
        .filter { it.second > 0.0 }
        .sortedByDescending { it.second }
        .map { it.first }
    }

    /**
     * Calculates a multi-dimensional relevance score for a product based on tokens.
     */
    private fun calculateRelevanceScore(
        tokens: List<String>,
        product: Product,
        categoryBoost: Set<String>
    ): Double {
        var score = 0.0
        val nameLower = product.name.lowercase()
        val descLower = product.description.lowercase()

        tokens.forEach { token ->
            // Title Match (High Weight)
            if (nameLower.contains(token)) score += 10.0
            
            // Description Match (Medium Weight)
            if (descLower.contains(token)) score += 3.0
            
            // Exact Match Bonus
            if (nameLower == token) score += 20.0
        }

        // Category Boosting
        if (categoryBoost.contains(product.categoryId)) {
            score *= 1.5
        }

        // Quality Signal (Rating / Review Count)
        score += (product.rating * 0.5) + (ln(1.0 + product.reviewCount) * 0.2)

        return score
    }

    /**
     * Implements a Levenshtein distance algorithm for fuzzy string matching.
     */
    fun calculateFuzzyDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
