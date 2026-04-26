package com.example.myapplication.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val categoryId: String,
    val imageUrl: String,
    val description: String,
    val rating: Double = 4.5,
    val reviewCount: Int = 0,
    val sellerId: String,
    val isNew: Boolean = true
)

data class Seller(
    val id: String,
    val name: String,
    val email: String,
    val rating: Double,
    val totalSales: Int,
    val profileImageUrl: String
)

data class Driver(
    val id: String,
    val name: String,
    val vehicleType: String,
    val currentLat: Double,
    val currentLng: Double,
    val isAvailable: Boolean = true
)

data class Order(
    val id: String,
    val productId: String,
    val buyerId: String,
    val sellerId: String,
    val driverId: String?,
    val status: OrderStatus,
    val timestamp: Long
)

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)
