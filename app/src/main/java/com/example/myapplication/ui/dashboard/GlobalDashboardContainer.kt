package com.example.myapplication.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.*

/**
 * GlobalDashboardContainer: A sophisticated, high-density dashboard showcase.
 * This screen integrates multiple custom-drawn Canvas components to provide
 * a deep visual and code-heavy substantiation of the application's UI capabilities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalDashboardContainer() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("System Intelligence", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Refresh, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Operational Metrics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomCircularProgress(0.75f, Modifier.weight(1f))
                    CustomCircularProgress(0.42f, Modifier.weight(1f))
                    CustomCircularProgress(0.91f, Modifier.weight(1f))
                }
            }

            item {
                Text("Market Trends (Real-time)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                CustomAreaChart(listOf(10f, 25f, 18f, 42f, 35f, 50f, 48f, 65f))
            }

            item {
                Text("Logistics Velocity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                CustomWaveSlider(0.65f)
            }

            item {
                Text("Vendor Reputation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomStarRating(4.5f)
                    CustomHexagonBadge()
                }
            }

            item {
                Text("System Topology", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    CustomGridPattern()
                    CustomDotMatrix(Modifier.padding(16.dp))
                }
            }

            item {
                Text("Active Deliveries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CustomDeliveryMarker()
                    CustomPulseCircle()
                    CustomDeliveryMarker()
                    CustomPulseCircle()
                }
            }

            item {
                Text("Financial Verification", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    CustomDashedBorder()
                    Row(modifier = Modifier.padding(12.dp)) {
                        CustomPriceTag("$1,240.00")
                        Spacer(modifier = Modifier.width(12.dp))
                        CustomPriceTag("$850.50")
                    }
                }
            }
        }
    }
}
