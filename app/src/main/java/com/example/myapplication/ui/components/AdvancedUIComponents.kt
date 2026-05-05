package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * AdvancedUIComponents: A suite of high-complexity data visualization components
 * using the Jetpack Compose Canvas API.
 */

@Composable
fun MarketplaceHeatmap(data: List<List<Float>>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val rows = data.size
        val cols = data.firstOrNull()?.size ?: 1
        val cellWidth = size.width / cols
        val cellHeight = size.height / rows

        data.forEachIndexed { r, row ->
            row.forEachIndexed { c, value ->
                val color = Color(0xFF6200EE).copy(alpha = value.coerceIn(0f, 1f))
                drawRect(
                    color = color,
                    topLeft = Offset(c * cellWidth, r * cellHeight),
                    size = Size(cellWidth, cellHeight)
                )
            }
        }
    }
}

@Composable
fun BezierSalesGraph(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        if (data.size < 2) return@Canvas
        
        val path = Path()
        val stepX = size.width / (data.size - 1)
        val maxVal = data.maxOrNull() ?: 1f

        path.moveTo(0f, size.height - (data[0] / maxVal * size.height))

        for (i in 0 until data.size - 1) {
            val x1 = i * stepX
            val y1 = size.height - (data[i] / maxVal * size.height)
            val x2 = (i + 1) * stepX
            val y2 = size.height - (data[i + 1] / maxVal * size.height)

            val conX1 = x1 + (x2 - x1) / 2
            val conY1 = y1
            val conX2 = x1 + (x2 - x1) / 2
            val conY2 = y2

            path.cubicTo(conX1, conY1, conX2, conY2, x2, y2)
        }

        drawPath(path, Color(0xFF03DAC5), style = Stroke(width = 6f, cap = StrokeCap.Round))
    }
}

@Composable
fun LogisticsLoadIndicator(load: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(200.dp, 40.dp)) {
        val cornerRadius = CornerRadius(20f, 20f)
        
        // Background track
        drawRoundRect(
            color = Color.LightGray.copy(0.3f),
            cornerRadius = cornerRadius
        )
        
        // Liquid segments
        val segments = 10
        val segWidth = size.width / segments
        for (i in 0 until (load * segments).toInt()) {
            drawRoundRect(
                color = if (load > 0.8f) Color.Red else Color.Green,
                topLeft = Offset(i * segWidth + 4f, 4f),
                size = Size(segWidth - 8f, size.height - 8f),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }
    }
}

@Composable
fun DynamicRadarChart(metrics: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2
        val sides = metrics.size
        val angleStep = (2 * PI / sides).toFloat()

        // Draw Web
        for (i in 1..4) {
            val path = Path()
            val r = radius * (i / 4f)
            for (j in 0 until sides) {
                val x = center.x + r * cos(j * angleStep)
                val y = center.y + r * sin(j * angleStep)
                if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, Color.Gray.copy(0.2f), style = Stroke(width = 2f))
        }

        // Draw Metrics Path
        val metricPath = Path()
        metrics.forEachIndexed { i, value ->
            val r = radius * value.coerceIn(0f, 1f)
            val x = center.x + r * cos(i * angleStep)
            val y = center.y + r * sin(i * angleStep)
            if (i == 0) metricPath.moveTo(x, y) else metricPath.lineTo(x, y)
        }
        metricPath.close()
        drawPath(metricPath, Color(0xFF6200EE).copy(0.5f))
        drawPath(metricPath, Color(0xFF6200EE), style = Stroke(width = 4f))
    }
}

@Composable
fun ConcentricActivityRing(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(150.dp)) {
        val stroke = 16.dp.toPx()
        val spacing = 20.dp.toPx()
        
        data.forEachIndexed { i, progress ->
            val r = (size.minDimension / 2) - (i * spacing)
            drawArc(
                color = Color.LightGray.copy(0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = stroke)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(Color.Cyan, Color.Blue)),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}
