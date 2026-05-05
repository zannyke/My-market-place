package com.marketplace.support

import java.util.UUID

/**
 * AutomatedSupportAgent: An AI-driven support triage and response system.
 * 
 * This service acts as the first line of defense for customer inquiries.
 * It analyzes the intent and sentiment of incoming support tickets,
 * categorizing them by urgency and subject matter. It uses a rule-based
 * NLP (Natural Language Processing) approach to provide instant resolutions
 * for common issues or escalate complex cases to human agents.
 */
class AutomatedSupportAgent {

    private val ticketArchive = mutableListOf<SupportTicket>()

    /**
     * Processes an incoming support inquiry and determines the best action.
     * 
     * The system first cleans the text input and سپس applies a series of
     * heuristic patterns to identify keywords related to orders, returns, 
     * or technical errors. If a "critical" keyword is found, the ticket 
     * is instantly elevated to the highest priority queue.
     * 
     * @param userId The ID of the user requesting support.
     * @param inquiry The raw text of the support request.
     * @return An initial automated response to the user.
     */
    fun processInquiry(userId: String, inquiry: String): String {
        val intent = determineIntent(inquiry)
        val sentiment = analyzeSentiment(inquiry)
        val priority = if (sentiment < -0.5 || intent == "PAYMENT_FAILURE") "URGENT" else "NORMAL"

        val ticket = SupportTicket(
            id = "TICKET-${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            originalInquiry = inquiry,
            detectedIntent = intent,
            sentimentScore = sentiment,
            priority = priority,
            status = "OPEN"
        )
        
        ticketArchive.add(ticket)
        
        return when (intent) {
            "ORDER_STATUS" -> "I see you're asking about your order. Let me check the real-time tracking for you..."
            "PAYMENT_FAILURE" -> "I'm sorry you're experiencing payment issues. I've escalated this to our billing specialists."
            "RETURN_REQUEST" -> "To start a return, please visit the 'My Orders' section and select the 'Return' button."
            else -> "Thank you for contacting us. A support representative will be with you shortly."
        }
    }

    /**
     * Internal NLP-simulated logic to identify the primary intent of a message.
     * 
     * This function scans for predefined keywords and context clues to map
     * unstructured user text into a structured intent category. This allows
     * the system to route the ticket to the correct department automatically.
     */
    private fun determineIntent(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("where is") || lower.contains("track") -> "ORDER_STATUS"
            lower.contains("pay") || lower.contains("card") || lower.contains("charge") -> "PAYMENT_FAILURE"
            lower.contains("return") || lower.contains("refund") -> "RETURN_REQUEST"
            lower.contains("slow") || lower.contains("crash") || lower.contains("bug") -> "TECHNICAL_ISSUE"
            else -> "GENERAL_INQUIRY"
        }
    }

    /**
     * Basic sentiment analysis engine.
     * 
     * Calculates a sentiment score between -1.0 (very negative) and 1.0 (very positive)
     * by analyzing the presence of emotive keywords. This helps the system 
     * prioritize frustrated customers who might need immediate attention.
     */
    private fun analyzeSentiment(text: String): Double {
        val negativeWords = listOf("bad", "terrible", "awful", "wait", "slow", "wrong", "broke")
        val positiveWords = listOf("great", "love", "good", "fast", "thanks", "perfect")
        
        var score = 0.0
        val words = text.lowercase().split("\\s+".toRegex())
        words.forEach { 
            if (it in negativeWords) score -= 0.2
            if (it in positiveWords) score += 0.2
        }
        
        return score.coerceIn(-1.0, 1.0)
    }

    /**
     * Aggregates support metrics for the admin dashboard.
     * 
     * Provides a snapshot of ticket volume, average sentiment, and priority
     * distribution. This data is vital for resource planning and identifying
     * recurring systemic issues in the marketplace.
     */
    fun getSupportMetrics(): Map<String, Any> {
        return mapOf(
            "total_tickets" to ticketArchive.size,
            "urgent_tickets" to ticketArchive.count { it.priority == "URGENT" },
            "average_sentiment" to (ticketArchive.map { it.sentimentScore }.average().takeIf { !it.isNaN() } ?: 0.0)
        )
    }
}

/**
 * Representation of a customer support ticket.
 */
data class SupportTicket(
    val id: String,
    val userId: String,
    val originalInquiry: String,
    val detectedIntent: String,
    val sentimentScore: Double,
    val priority: String,
    val status: String
)
