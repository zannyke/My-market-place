package com.example.myapplication.ui.driver.dashboard

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.OrderStatus
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.OrderRepository
import kotlinx.coroutines.flow.*

class DriverViewModel : ViewModel() {
    val activeDeliveries: StateFlow<List<Order>> = OrderRepository.orders
        .map { orders -> orders.filter { it.driverId == "d1" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentLat = -1.286389
    val currentLng = 36.817223
}
