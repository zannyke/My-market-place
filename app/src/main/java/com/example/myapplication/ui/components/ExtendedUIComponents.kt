package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CustomStarRating(rating: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(150.dp, 30.dp)) {
        val starWidth = 30.dp.toPx()
        for (i in 0 until 5) {
            val center = Offset(i * starWidth + starWidth / 2, size.height / 2)
            drawStar(center, starWidth / 2, if (i < rating.toInt()) Color(0xFFFFD700) else Color.LightGray)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    for (i in 0 until 10) {
        val angle = i * kotlin.math.PI / 5
        val r = if (i % 2 == 0) radius else radius * 0.4f
        val x = center.x + r * kotlin.math.sin(angle).toFloat()
        val y = center.y - r * kotlin.math.cos(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

@Composable
fun CustomHexagonBadge(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(60.dp)) {
        val path = Path()
        val radius = size.minDimension / 2
        for (i in 0 until 6) {
            val angle = i * kotlin.math.PI / 3
            val x = center.x + radius * kotlin.math.cos(angle).toFloat()
            val y = center.y + radius * kotlin.math.sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color = Color(0xFF4CAF50))
    }
}

@Composable
fun CustomPulseCircle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(Color.Blue.copy(0.1f), radius = size.minDimension / 2)
        drawCircle(Color.Blue, radius = size.minDimension / 4)
    }
}

@Composable
fun CustomDashedBorder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            color = Color.Gray,
            style = Stroke(width = 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        )
    }
}
