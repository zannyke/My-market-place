package com.example.myapplication.util

import com.example.myapplication.data.model.*
import java.util.UUID
import kotlin.random.Random

/**
 * MockDataGenerator: A high-density data generation utility for the My Market Place ecosystem.
 * This class provides sophisticated logic to generate thousands of data points for testing
 * and UI substantiation.
 */
object MockDataGenerator {

    private val random = Random(System.currentTimeMillis())

    fun generateUsers(count: Int = 50): List<User> {
        val names = listOf("John Doe", "Jane Smith", "Michael Johnson", "Emily Brown", "Chris Wilson", "Sarah Davis")
        val roles = listOf("Buyer", "Seller", "Driver")
        
        return (1..count).map {
            User(
                id = UUID.randomUUID().toString(),
                name = names.random(),
                email = "user${it}@example.com",
                role = roles.random(),
                phoneNumber = "+1-555-${random.nextInt(1000, 9999)}",
                address = "${random.nextInt(1, 999)} Marketplace Ave, Tech City",
                joinDate = System.currentTimeMillis() - (random.nextLong(1, 365) * 24 * 60 * 60 * 1000),
                profilePicUrl = "https://i.pravatar.cc/150?u=${it}",
                balance = random.nextDouble(100.0, 10000.0)
            )
        }
    }

    fun generateProducts(count: Int = 200, sellers: List<User>): List<Product> {
        val categories = listOf("Electronics", "Fashion", "Home", "Sports", "Beauty")
        val prefixes = listOf("Pro", "Elite", "Ultra", "Max", "Premium", "NextGen")
        val suffixes = listOf("X1", "S2", "Air", "Z", "Neo", "Core")
        
        return (1..count).map {
            Product(
                id = UUID.randomUUID().toString(),
                name = "${prefixes.random()} ${suffixes.random()} ${it}",
                price = random.nextDouble(19.99, 2999.99),
                categoryId = categories.random(),
                imageUrl = "https://picsum.photos/seed/${it}/400/400",
                description = "High-performance ${categories.random()} product featuring state-of-the-art technology and premium materials.",
                rating = random.nextDouble(3.5, 5.0),
                reviewCount = random.nextInt(0, 500),
                sellerId = sellers.filter { u -> u.role == "Seller" }.random().id,
                isNew = random.nextBoolean()
            )
        }
    }

    fun generateOrders(count: Int = 1000, users: List<User>, products: List<Product>): List<Order> {
        val buyers = users.filter { it.role == "Buyer" }
        val drivers = users.filter { it.role == "Driver" }
        val statuses = OrderStatus.values()

        return (1..count).map {
            val product = products.random()
            Order(
                id = "ORD-${it.toString().padStart(5, '0')}",
                productId = product.id,
                buyerId = buyers.random().id,
                sellerId = product.sellerId,
                driverId = if (random.nextBoolean()) drivers.random().id else null,
                status = statuses.random(),
                timestamp = System.currentTimeMillis() - (random.nextLong(0, 30) * 24 * 60 * 60 * 1000)
            )
        }
    }

    fun generateTransactions(orders: List<Order>): List<Transaction> {
        return orders.map { order ->
            Transaction(
                id = "TXN-${UUID.randomUUID().toString().take(8)}",
                orderId = order.id,
                amount = random.nextDouble(50.0, 5000.0),
                timestamp = order.timestamp + 5000,
                paymentMethod = PaymentMethod.values().random(),
                status = TransactionStatus.values().random()
            )
        }
    }

    fun generateAnalytics(sellerId: String): AnalyticsData {
        return AnalyticsData(
            totalRevenue = random.nextDouble(10000.0, 500000.0),
            salesCount = random.nextInt(100, 5000),
            growthRate = random.nextDouble(-0.1, 0.4),
            topSellingCategories = listOf(
                "Electronics" to random.nextDouble(40.0, 60.0),
                "Fashion" to random.nextDouble(20.0, 30.0),
                "Home" to random.nextDouble(10.0, 20.0)
            ),
            hourlySales = (0..23).associateWith { random.nextDouble(0.0, 1000.0) }
        )
    }

    fun generateRoute(driverId: String): DeliveryRoute {
        return DeliveryRoute(
            id = UUID.randomUUID().toString(),
            driverId = driverId,
            orderIds = (1..5).map { "ORD-${random.nextInt(10000, 99999)}" },
            startLat = -1.286389 + (random.nextDouble(-0.05, 0.05)),
            startLng = 36.817223 + (random.nextDouble(-0.05, 0.05)),
            endLat = -1.286389 + (random.nextDouble(-0.1, 0.1)),
            endLng = 36.817223 + (random.nextDouble(-0.1, 0.1)),
            optimizedPath = (1..10).map {
                (-1.286389 + random.nextDouble(-0.1, 0.1)) to (36.817223 + random.nextDouble(-0.1, 0.1))
            }
        )
    }

    fun generateReviews(productId: String, count: Int = 10): List<Review> {
        val comments = listOf(
            "Amazing quality, worth every penny!",
            "Fast shipping and great customer service.",
            "The product is okay, but the packaging was damaged.",
            "Exactly what I was looking for. 5 stars!",
            "A bit expensive compared to other sellers.",
            "I love the design and the build quality is top-notch.",
            "Took a while to arrive, but the product is great.",
            "Very satisfied with my purchase. Will buy again.",
            "The colors are slightly different from the pictures.",
            "Highly recommend this seller!"
        )
        val names = listOf("Alex", "Sam", "Chris", "Jordan", "Taylor", "Morgan", "Casey")
        
        return (1..count).map {
            Review(
                id = UUID.randomUUID().toString(),
                productId = productId,
                userName = names.random(),
                rating = random.nextInt(3, 6),
                comment = comments.random(),
                timestamp = System.currentTimeMillis() - (random.nextLong(0, 100) * 60 * 60 * 1000)
            )
        }
    }

    fun generateLargeAuditLog(count: Int = 500): List<String> {
        val actions = listOf("LOGIN", "LOGOUT", "PURCHASE", "REFUND", "SEARCH", "PROFILE_UPDATE", "PASSWORD_CHANGE")
        val statuses = listOf("SUCCESS", "FAILED", "PENDING", "BLOCKED")
        
        return (1..count).map {
            "LOG|${System.currentTimeMillis() - it * 1000}|${actions.random()}|USER-${random.nextInt(1000, 9999)}|${statuses.random()}|REQ-${UUID.randomUUID().toString().take(6)}"
        }
    }

    fun generateCategoryMetadata(): Map<String, Map<String, Any>> {
        return mapOf(
            "Electronics" to mapOf("tax" to 0.18, "insurance" to true, "shipping_weight" to 2.5, "shelf_life" to -1),
            "Fashion" to mapOf("tax" to 0.12, "insurance" to false, "shipping_weight" to 0.8, "shelf_life" to 24),
            "Home" to mapOf("tax" to 0.16, "insurance" to true, "shipping_weight" to 5.0, "shelf_life" to -1),
            "Beauty" to mapOf("tax" to 0.20, "insurance" false, "shipping_weight" to 0.3, "shelf_life" to 12)
        )
    }
}
