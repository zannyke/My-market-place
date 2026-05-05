package com.example.myapplication.data.repository

import com.example.myapplication.data.model.*
import com.example.myapplication.util.MockDataGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * UserRepository: Handles authentication, profile management, and user-specific marketplace data.
 */
class UserRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val users = MockDataGenerator.generateUsers(100)

    fun login(email: String): Boolean {
        val user = users.find { it.email == email }
        return if (user != null) {
            _currentUser.value = user
            true
        } else false
    }

    fun getUserById(id: String): User? = users.find { it.id == id }

    fun updateProfile(updatedUser: User) {
        _currentUser.value = updatedUser
        // logic to sync with DB/API
    }

    fun getDrivers(): List<User> = users.filter { it.role == "Driver" }
}

/**
 * AnalyticsRepository: Provides high-level business intelligence data for sellers and admins.
 */
class AnalyticsRepository {
    
    fun getSellerPerformance(sellerId: String): Flow<AnalyticsData> {
        return MutableStateFlow(MockDataGenerator.generateAnalytics(sellerId))
    }

    fun getPlatformOverview(): Flow<Map<String, Double>> {
        return MutableStateFlow(mapOf(
            "TotalGMV" to 1250000.0,
            "ActiveUsers" to 45000.0,
            "OrderVelocity" to 12.5,
            "RetentionRate" to 0.85
        ))
    }

    fun getCategoryDistribution(): Flow<List<Pair<String, Double>>> {
        return MutableStateFlow(listOf(
            "Electronics" to 45.0,
            "Fashion" to 25.0,
            "Home" to 15.0,
            "Others" to 15.0
        ))
    }
}
