package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Seller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SellerRepository {
    private val _sellers = MutableStateFlow(listOf(
        Seller("s1", "Tech World", "sales@techworld.com", 4.8, 1250, "https://images.unsplash.com/photo-1556740738-b6a63e27c4df?w=200"),
        Seller("s2", "Fresh Farm", "orders@freshfarm.com", 4.9, 3400, "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=200")
    ))
    val sellers: StateFlow<List<Seller>> = _sellers

    fun getSellerById(id: String): Seller? {
        return _sellers.value.find { it.id == id }
    }
}
