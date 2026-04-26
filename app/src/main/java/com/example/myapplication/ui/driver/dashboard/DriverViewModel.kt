package com.example.myapplication.ui.driver.dashboard

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DriverViewModel : ViewModel() {
    private val _activeDeliveries = MutableStateFlow(listOf(
        Order("ord1", "1", "user1", "s1", "d1", OrderStatus.PROCESSING, System.currentTimeMillis()),
        Order("ord3", "2", "user2", "s1", "d1", OrderStatus.SHIPPED, System.currentTimeMillis())
    ))
    val activeDeliveries: StateFlow<List<Order>> = _activeDeliveries

    val currentLat = -1.286389
    val currentLng = 36.817223
}
