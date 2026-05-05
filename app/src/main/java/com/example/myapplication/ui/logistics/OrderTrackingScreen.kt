package com.example.myapplication.ui.logistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(orderId: String = "ORD-99231") {
    val steps = listOf(
        TrackingStep("Order Placed", "Dec 12, 10:00 AM", OrderStatus.PENDING, true),
        TrackingStep("Payment Confirmed", "Dec 12, 10:15 AM", OrderStatus.PROCESSING, true),
        TrackingStep("Shipped from Warehouse", "Dec 13, 09:00 AM", OrderStatus.SHIPPED, true),
        TrackingStep("Out for Delivery", "Dec 14, 08:30 AM", OrderStatus.DELIVERED, false),
        TrackingStep("Delivered", "Estimating...", OrderStatus.DELIVERED, false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order #$orderId") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Expected Delivery", style = MaterialTheme.typography.labelMedium)
                        Text("Monday, Dec 15", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(steps) { step ->
                    TrackingStepItem(step)
                }
            }

            Button(
                onClick = { /* TODO: Support */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Contact Support")
            }
        }
    }
}

data class TrackingStep(
    val title: String,
    val subtitle: String,
    val status: OrderStatus,
    val isCompleted: Boolean
)

@Composable
fun TrackingStepItem(step: TrackingStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (step.isCompleted) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                step.title, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = if (step.isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (step.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(step.subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
