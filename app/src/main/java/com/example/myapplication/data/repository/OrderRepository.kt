package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

import com.example.myapplication.MyApplication
import com.example.myapplication.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object OrderRepository {
    private val orderDao = MyApplication.database.orderDao()

    val orders: Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun placeOrder(productId: String, buyerId: String, sellerId: String) {
        val newOrder = Order(
            id = "ord${UUID.randomUUID().toString().take(5)}",
            productId = productId,
            buyerId = buyerId,
            sellerId = sellerId,
            driverId = null,
            status = OrderStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )
        orderDao.insertOrder(newOrder.toEntity())
    }

    private fun Order.toEntity() = OrderEntity(
        id, productId, buyerId, sellerId, driverId, status, timestamp
    )

    private fun OrderEntity.toDomain() = Order(
        id, productId, buyerId, sellerId, driverId, status, timestamp
    )
}
