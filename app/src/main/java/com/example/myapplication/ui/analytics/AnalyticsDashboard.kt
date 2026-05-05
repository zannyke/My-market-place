package com.example.myapplication.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AnalyticsData
import com.example.myapplication.util.MockDataGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboard(sellerId: String = "seller-123") {
    val analytics = MockDataGenerator.generateAnalytics(sellerId)

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Business Intelligence", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text("Performance Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                RevenueChart(analytics.hourlySales)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BIStatCard("Growth", "+${(analytics.growthRate * 100).toInt()}%", Icons.Default.TrendingUp, Modifier.weight(1f))
                    BIStatCard("Orders", analytics.salesCount.toString(), Icons.Default.ShoppingBag, Modifier.weight(1f))
                }
            }

            item {
                Text("Category Distribution", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(analytics.topSellingCategories) { (name, percent) ->
                CategoryProgress(name, percent.toFloat() / 100f)
            }
        }
    }
}

@Composable
fun RevenueChart(data: Map<Int, Double>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val points = data.values.toList()
                val max = points.maxOrNull() ?: 1.0
                val path = Path()
                
                points.forEachIndexed { index, value ->
                    val x = index * (size.width / (points.size - 1))
                    val y = size.height - (value / max * size.height).toFloat()
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                
                drawPath(path, color = Color(0xFF6200EE), style = Stroke(width = 4f))
            }
        }
    }
}

@Composable
fun BIStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun CategoryProgress(name: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
