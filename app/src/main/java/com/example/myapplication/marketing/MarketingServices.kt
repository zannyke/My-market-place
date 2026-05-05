package com.example.myapplication.marketing

import com.example.myapplication.data.model.*
import java.util.*

/**
 * MarketingAutomationEngine: Manages complex marketing campaigns and user targeting.
 */
class MarketingAutomationEngine {

    private val campaigns = mutableListOf<Campaign>()

    fun createCampaign(name: String, targetRole: String, discount: Double): Campaign {
        val campaign = Campaign(
            id = UUID.randomUUID().toString(),
            name = name,
            targetRole = targetRole,
            discountValue = discount,
            isActive = true,
            conversionRate = 0.0
        )
        campaigns.add(campaign)
        return campaign
    }

    fun calculateTargetedOffer(user: User, products: List<Product>): List<Product> {
        // Complex targeting logic based on user role and product pricing
        return products.filter { product ->
            when (user.role) {
                "Buyer" -> product.price < user.balance * 0.1
                "Seller" -> product.isNew
                else -> false
            }
        }.take(5)
    }

    fun generateCampaignReport(): String {
        return campaigns.joinToString("\n") { 
            "Campaign: ${it.name} | Targeted: ${it.targetRole} | Conversion: ${it.conversionRate * 100}%"
        }
    }
}

data class Campaign(
    val id: String,
    val name: String,
    val targetRole: String,
    val discountValue: Double,
    val isActive: Boolean,
    var conversionRate: Double
)

package com.example.myapplication.notifications

import com.example.myapplication.data.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * NotificationDispatchService: Orchestrates push notifications and in-app alerts.
 */
class NotificationDispatchService {

    private val _notifications = MutableSharedFlow<Notification>()
    val notifications = _notifications.asSharedFlow()

    suspend fun sendNotification(userId: String, title: String, message: String) {
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        _notifications.emit(notification)
        println("Notifications: Sent to $userId - $title")
    }

    fun buildMarketingMessage(user: User, campaign: com.example.myapplication.marketing.Campaign): String {
        return "Hello ${user.name}! Enjoy a special ${campaign.discountValue * 100}% discount on your next purchase as part of our ${campaign.name} event."
    }
}
