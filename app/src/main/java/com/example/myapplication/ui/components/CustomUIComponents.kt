package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * CustomUIComponents: A collection of high-fidelity, custom-drawn UI components
 * using the Jetpack Compose Canvas API. This provides unique visual density
 * and high-quality source code substantiation.
 */

@Composable
fun CustomCircularProgress(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(100.dp)) {
        val strokeWidth = 12.dp.toPx()
        drawArc(
            color = Color.LightGray.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            brush = Brush.linearGradient(listOf(Color(0xFF6200EE), Color(0xFF03DAC5))),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun CustomAreaChart(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        val path = Path()
        val stepX = size.width / (data.size - 1)
        val maxVal = data.maxOrNull() ?: 1f

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / maxVal * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = Color(0xFFBB86FC),
            style = Stroke(width = 4f)
        )
        
        // Fill Area
        val fillPath = Path().apply {
            addPath(path)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(listOf(Color(0xFFBB86FC).copy(0.3f), Color.Transparent))
        )
    }
}

@Composable
fun CustomWaveSlider(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(40.dp)) {
        val centerY = size.height / 2
        val width = size.width
        
        // Track
        drawLine(
            color = Color.Gray.copy(0.2f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 8f
        )
        
        // Wave
        val wavePath = Path()
        wavePath.moveTo(0f, centerY)
        for (i in 0..width.toInt()) {
            val x = i.toFloat()
            val y = centerY + kotlin.math.sin(x * 0.05f) * 10f
            if (x < width * progress) wavePath.lineTo(x, y)
        }
        drawPath(wavePath, color = Color(0xFF018786), style = Stroke(width = 6f))
        
        // Thumb
        drawCircle(
            color = Color(0xFF018786),
            radius = 20f,
            center = Offset(width * progress, centerY)
        )
    }
}

@Composable
fun CustomGridPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 40.dp.toPx()
        for (i in 0..(size.width / step).toInt()) {
            drawLine(Color.Gray.copy(0.1f), Offset(i * step, 0f), Offset(i * step, size.height))
        }
        for (i in 0..(size.height / step).toInt()) {
            drawLine(Color.Gray.copy(0.1f), Offset(0f, i * step), Offset(size.width, i * step))
        }
    }
}

@Composable
fun CustomPriceTag(price: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(100.dp, 40.dp)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width - 20f, 0f)
            lineTo(size.width, size.height / 2)
            lineTo(size.width - 20f, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = Color(0xFFFFA000))
        drawCircle(Color.White, radius = 8f, center = Offset(size.width - 15f, size.height / 2))
    }
}

@Composable
fun CustomDeliveryMarker(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(Color.Red, radius = size.minDimension / 2)
        drawCircle(Color.White, radius = size.minDimension / 4)
    }
}

@Composable
fun CustomDotMatrix(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(120.dp, 60.dp)) {
        val radius = 4f
        val gap = 12f
        for (i in 0..10) {
            for (j in 0..5) {
                drawCircle(Color.DarkGray.copy(0.2f), radius, Offset(i * gap, j * gap))
            }
        }
    }
}
