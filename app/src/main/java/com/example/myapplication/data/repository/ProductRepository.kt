package com.example.myapplication.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGroceryStore
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Product

import com.example.myapplication.MyApplication
import com.example.myapplication.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ProductRepository {
    private val productDao = MyApplication.database.productDao()

    val categories = listOf(
        Category("1", "Electronics", Icons.Default.Computer),
        Category("2", "Fashion", Icons.Default.Checkroom),
        Category("3", "Food", Icons.Default.Fastfood),
        Category("4", "Groceries", Icons.Default.LocalGroceryStore)
    )

    private val mockProducts = listOf(
        Product("1", "Smartphone X", 999.0, "1", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400", "Latest model with AI camera", 4.8, 120, "s1"),
        Product("2", "Designer Jacket", 120.0, "2", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400", "Waterproof and stylish", 4.5, 85, "s1"),
        Product("3", "Organic Apples", 5.0, "4", "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=400", "Fresh from the farm", 4.9, 210, "s2"),
        Product("4", "Wireless Buds", 150.0, "1", "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400", "Noise cancelling", 4.7, 95, "s2"),
        Product("5", "Classic Watch", 250.0, "2", "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400", "Elegant and timeless", 4.6, 54, "s1")
    )

    suspend fun seedDatabase() {
        val entities = mockProducts.map { it.toEntity() }
        productDao.insertProducts(entities)
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun filterProducts(query: String, categoryId: String? = null): Flow<List<Product>> {
        return getAllProducts().map { products ->
            products.filter { product ->
                val matchesQuery = product.name.contains(query, ignoreCase = true) || 
                                 product.description.contains(query, ignoreCase = true)
                val matchesCategory = categoryId == null || product.categoryId == categoryId
                matchesQuery && matchesCategory
            }
        }
    }

    private fun Product.toEntity() = ProductEntity(
        id, name, price, categoryId, imageUrl, description, rating, reviewCount, sellerId, isNew
    )

    private fun ProductEntity.toDomain() = Product(
        id, name, price, categoryId, imageUrl, description, rating, reviewCount, sellerId, isNew
    )
}
