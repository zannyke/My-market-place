package com.example.myapplication.ui.seller.dashboard

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.repository.ProductRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class SellerDashboardViewModel : ViewModel() {
    val inventory: StateFlow<List<Product>> = ProductRepository.getAllProducts()
        .map { products -> products.filter { it.sellerId == "s1" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSales = 1250
    val rating = 4.8
    val revenue = 15400.0

    fun deleteProduct(productId: String) {
        _inventory.value = _inventory.value.filter { it.id != productId }
    }
}
