package com.example.myapplication.ui.logistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.*

/**
 * LogisticsOperationsCenter: A mission-control style dashboard for managing
 * global logistics and delivery optimization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogisticsOperationsCenter() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logistics Mission Control", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text("Global Route Topology", style = MaterialTheme.typography.titleMedium)
                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    CustomGridPattern()
                    MarketplaceHeatmap(listOf(
                        listOf(0.1f, 0.5f, 0.2f),
                        listOf(0.8f, 0.3f, 0.9f),
                        listOf(0.2f, 0.7f, 0.4f)
                    ))
                }
            }

            item {
                Text("Carrier Performance (Radar)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                DynamicRadarChart(listOf(0.8f, 0.9f, 0.7f, 0.6f, 0.85f))
            }

            item {
                Text("Active Logistics Load", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                LogisticsLoadIndicator(0.78f)
            }

            item {
                Text("Delivery Velocity (Bezier)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                BezierSalesGraph(listOf(20f, 40f, 35f, 60f, 55f, 80f))
            }

            item {
                Text("Fulfillment Ring Status", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                ConcentricActivityRing(listOf(0.9f, 0.75f, 0.4f))
            }
        }
    }
}
