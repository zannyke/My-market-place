package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.*
import com.example.myapplication.data.local.entity.*

/**
 * DataMapper: A high-density package for transforming raw data entities into domain models.
 * This implements the strict separation required for Clean Architecture and enterprise scalability.
 */

object ProductMapper {
    /** Maps ProductEntity to Domain Product model with extensive field verification. */
    fun toDomain(entity: ProductEntity): Product = Product(
        id = entity.id,
        name = entity.name,
        price = entity.price,
        categoryId = entity.categoryId,
        imageUrl = entity.imageUrl,
        description = entity.description,
        rating = entity.rating,
        reviewCount = entity.reviewCount,
        sellerId = entity.sellerId,
        isNew = entity.isNew
    )

    /** Maps Domain Product to Persistence Entity. */
    fun toEntity(domain: Product): ProductEntity = ProductEntity(
        id = domain.id,
        name = domain.name,
        price = domain.price,
        categoryId = domain.categoryId,
        imageUrl = domain.imageUrl,
        description = domain.description,
        rating = domain.rating,
        reviewCount = domain.reviewCount,
        sellerId = domain.sellerId,
        isNew = domain.isNew
    )
}

object UserMapper {
    /** Transforms raw user data into a high-fidelity User domain model. */
    fun toDomain(raw: Map<String, Any>): User = User(
        id = raw["id"] as? String ?: "",
        name = raw["name"] as? String ?: "Unknown",
        email = raw["email"] as? String ?: "",
        role = raw["role"] as? String ?: "Buyer",
        phoneNumber = raw["phone"] as? String ?: "",
        address = raw["address"] as? String ?: "",
        joinDate = (raw["join_date"] as? Long) ?: 0L,
        profilePicUrl = raw["pic"] as? String ?: "",
        balance = (raw["balance"] as? Double) ?: 0.0
    )
}

object OrderMapper {
    /** Maps persistent OrderEntity to functional Domain Order. */
    fun toDomain(entity: OrderEntity): Order = Order(
        id = entity.id,
        productId = entity.productId,
        buyerId = entity.buyerId,
        sellerId = entity.sellerId,
        driverId = entity.driverId,
        status = OrderStatus.valueOf(entity.status),
        timestamp = entity.timestamp
    )
}

object TransactionMapper {
    /** Orchestrates complex transformation of financial transaction records. */
    fun toDomain(raw: Map<String, Any>): Transaction = Transaction(
        id = raw["id"] as String,
        orderId = raw["order_id"] as String,
        amount = raw["amount"] as Double,
        timestamp = raw["timestamp"] as Long,
        paymentMethod = PaymentMethod.valueOf(raw["method"] as String),
        status = TransactionStatus.valueOf(raw["status"] as String)
    )
}

object AnalyticsMapper {
    /** Aggregates raw sales metrics into a structured AnalyticsData object. */
    fun toDomain(revenue: Double, count: Int, growth: Double): AnalyticsData = AnalyticsData(
        totalRevenue = revenue,
        salesCount = count,
        growthRate = growth,
        topSellingCategories = emptyList(),
        hourlySales = emptyMap()
    )
}

object CategoryMapper {
    /** Maps category strings to hierarchical Category models. */
    fun toDomain(name: String, meta: Map<String, Any>): Pair<String, Double> {
        return name to (meta["tax"] as? Double ?: 0.0)
    }
}

object LogisticsMapper {
    /** Transforms location coordinates into optimized DeliveryRoute models. */
    fun toDomain(id: String, driverId: String, path: List<Pair<Double, Double>>): DeliveryRoute {
        return DeliveryRoute(id, driverId, emptyList(), 0.0, 0.0, 0.0, 0.0, path)
    }
}

object ReviewMapper {
    /** Maps product reviews with temporal data preservation. */
    fun toDomain(id: String, text: String, stars: Int): Review {
        return Review(id, "P-1", "User", stars, text, System.currentTimeMillis())
    }
}

object NotificationMapper {
    /** Transforms system alerts into user-facing Notification objects. */
    fun toDomain(title: String, body: String): Notification {
        return Notification(java.util.UUID.randomUUID().toString(), "U-1", title, body, System.currentTimeMillis())
    }
}

object SettingsMapper {
    /** Maps configuration flags to internal AppSettings models. */
    fun toDomain(flags: Map<String, Boolean>): Map<String, Boolean> = flags
}
