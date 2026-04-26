package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val categoryId: String,
    val imageUrl: String,
    val description: String,
    val rating: Double,
    val reviewCount: Int,
    val sellerId: String,
    val isNew: Boolean
)
