package com.example.myapplication.ui.seller.dashboard

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SellerDashboardViewModel : ViewModel() {
    private val _inventory = MutableStateFlow(ProductRepository.products.filter { it.sellerId == "s1" })
    val inventory: StateFlow<List<Product>> = _inventory

    val totalSales = 1250
    val rating = 4.8
    val revenue = 15400.0

    fun deleteProduct(productId: String) {
        _inventory.value = _inventory.value.filter { it.id != productId }
    }
}
