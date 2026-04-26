package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

object OrderRepository {
    private val _orders = MutableStateFlow<List<Order>>(listOf(
        Order("ord1", "1", "user1", "s1", "d1", OrderStatus.PROCESSING, System.currentTimeMillis()),
        Order("ord2", "3", "user1", "s2", null, OrderStatus.PENDING, System.currentTimeMillis())
    ))
    val orders: StateFlow<List<Order>> = _orders

    fun placeOrder(productId: String, buyerId: String, sellerId: String) {
        val newOrder = Order(
            id = "ord${UUID.randomUUID().toString().take(5)}",
            productId = productId,
            buyerId = buyerId,
            sellerId = sellerId,
            driverId = null,
            status = OrderStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )
        _orders.value = _orders.value + newOrder
    }
}
