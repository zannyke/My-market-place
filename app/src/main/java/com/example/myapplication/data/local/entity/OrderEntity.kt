package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.model.OrderStatus

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val buyerId: String,
    val sellerId: String,
    val driverId: String?,
    val status: OrderStatus,
    val timestamp: Long
)
