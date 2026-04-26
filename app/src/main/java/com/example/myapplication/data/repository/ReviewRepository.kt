package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ReviewRepository {
    private val _reviews = MutableStateFlow<List<Review>>(listOf(
        Review("r1", "1", "Alice", 5, "Amazing phone, very fast!", System.currentTimeMillis()),
        Review("r2", "1", "Bob", 4, "Good battery life.", System.currentTimeMillis()),
        Review("r3", "2", "Charlie", 5, "Very stylish and warm.", System.currentTimeMillis())
    ))
    val reviews: StateFlow<List<Review>> = _reviews

    fun getReviewsForProduct(productId: String): List<Review> {
        return _reviews.value.filter { it.productId == productId }
    }
}
