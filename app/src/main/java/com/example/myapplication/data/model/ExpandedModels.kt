package com.example.myapplication.data.model

import java.util.*

// Existing Models expanded with more fields
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // "Buyer", "Seller", "Driver"
    val phoneNumber: String,
    val address: String,
    val joinDate: Long,
    val profilePicUrl: String,
    val balance: Double = 0.0
)

data class Transaction(
    val id: String,
    val orderId: String,
    val amount: Double,
    val timestamp: Long,
    val paymentMethod: PaymentMethod,
    val status: TransactionStatus
)

enum class PaymentMethod {
    CREDIT_CARD, PAYPAL, BANK_TRANSFER, CRYPTO
}

enum class TransactionStatus {
    PENDING, SUCCESS, FAILED, REFUNDED
}

data class AnalyticsData(
    val totalRevenue: Double,
    val salesCount: Int,
    val growthRate: Double,
    val topSellingCategories: List<Pair<String, Double>>,
    val hourlySales: Map<Int, Double>
)

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

data class CartItem(
    val productId: String,
    val quantity: Int,
    val addedAt: Long
)

data class Wishlist(
    val userId: String,
    val productIds: List<String>
)

// Logistics extension
data class DeliveryRoute(
    val id: String,
    val driverId: String,
    val orderIds: List<String>,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val optimizedPath: List<Pair<Double, Double>>
)
