package com.hulusimsek.cryptoapp.presentation.details_screen.views

import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hulusimsek.cryptoapp.domain.model.Candle
import kotlin.math.abs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hulusimsek.cryptoapp.domain.model.KlineModel
import com.hulusimsek.cryptoapp.presentation.theme.dusenKirmizi
import com.hulusimsek.cryptoapp.presentation.theme.yukselenYesil
import kotlin.math.abs
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun CandleStickChartView(
    klines: List<KlineModel>,
    modifier: Modifier = Modifier
) {
    var zoomState by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }

    val density = LocalDensity.current.density
    val candleWidthDp = 8.dp
    val candleSpacingDp = 4.dp
    val candleWidthPx = candleWidthDp.toPx(density)
    val candleSpacingPx = candleSpacingDp.toPx(density)

    val maxItems = klines.size

    Box(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Zoom seviyesini güncelle
                    zoomState = (zoomState * zoom).coerceIn(0.5f, 3f)

                    val canvasWidth = size.width
                    val totalWidth = (candleWidthPx * zoomState + candleSpacingPx) * maxItems - candleSpacingPx

                    // Kaydırma sınırlarını güncelle
                    offsetX = (offsetX + pan.x).coerceIn(
                        -totalWidth + canvasWidth,
                        0f
                    )
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Çizim alanının sınırlarını ayarla
            val totalWidth = (candleWidthPx * zoomState + candleSpacingPx) * maxItems - candleSpacingPx

            // Görünür mumlar için sınırları belirle
            val fromIndex = ((-offsetX) / (candleWidthPx * zoomState + candleSpacingPx)).toInt().coerceAtLeast(0)
            val toIndex = ((canvasWidth - offsetX) / (candleWidthPx * zoomState + candleSpacingPx)).toInt() + fromIndex

            if (fromIndex < toIndex && fromIndex < maxItems) {
                // Ekranın görünür kısmındaki mumları belirle
                val visibleKlips = klines.subList(fromIndex, minOf(toIndex, maxItems))

                val minLow = visibleKlips.minOfOrNull { it.lowPrice.toFloatOrNull() ?: Float.MAX_VALUE } ?: 0f
                val maxHigh = visibleKlips.maxOfOrNull { it.highPrice.toFloatOrNull() ?: Float.MIN_VALUE } ?: 1f
                val priceRange = maxHigh - minLow

                // Mumları çiz
                drawCandles(
                    visibleKlips,
                    candleWidthPx * zoomState,
                    candleSpacingPx,
                    canvasHeight,
                    minLow,
                    priceRange
                )

                // Y eksenini güncelle
                drawYLabels(minLow, maxHigh, priceRange, canvasHeight)

                // X eksenini güncelle
                drawXLabels(visibleKlips, canvasWidth, candleWidthPx * zoomState, candleSpacingPx)
            }
        }
    }
}

private fun DrawScope.drawCandles(
    klines: List<KlineModel>,
    candleWidth: Float,
    candleSpacing: Float,
    canvasHeight: Float,
    minLow: Float,
    priceRange: Float
) {
    val heightRatio = canvasHeight / priceRange

    klines.forEachIndexed { index, kline ->
        val openPrice = kline.openPrice.toFloatOrNull() ?: 0f
        val closePrice = kline.closePrice.toFloatOrNull() ?: 0f
        val highPrice = kline.highPrice.toFloatOrNull() ?: 0f
        val lowPrice = kline.lowPrice.toFloatOrNull() ?: 0f

        val candleX = index * (candleWidth + candleSpacing)
        val candleYOpen = canvasHeight - ((openPrice - minLow) * heightRatio)
        val candleYClose = canvasHeight - ((closePrice - minLow) * heightRatio)
        val candleYHigh = canvasHeight - ((highPrice - minLow) * heightRatio)
        val candleYLow = canvasHeight - ((lowPrice - minLow) * heightRatio)

        // Mumun gövdesi
        drawRect(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            topLeft = Offset(candleX, minOf(candleYOpen, candleYClose)),
            size = Size(candleWidth, Math.abs(candleYOpen - candleYClose))
        )

        // Mumun fitili
        drawLine(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            start = Offset(candleX + candleWidth / 2, candleYHigh),
            end = Offset(candleX + candleWidth / 2, candleYLow),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawYLabels(minLow: Float, maxHigh: Float, priceRange: Float, canvasHeight: Float) {
    val labelCount = 5
    val formatter = DecimalFormat("#,###.##")

    for (i in 0 until labelCount) {
        val labelValue = minLow + (priceRange / (labelCount - 1)) * i
        val yPos = canvasHeight - ((labelValue - minLow) * (canvasHeight / priceRange))

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                formatter.format(labelValue),
                20f,
                yPos,
                Paint().apply {
                    color = Color.Gray.toArgb()
                    textSize = 24f
                    textAlign = Paint.Align.LEFT
                }
            )
        }
    }
}

private fun DrawScope.drawXLabels(
    klines: List<KlineModel>,
    canvasWidth: Float,
    candleWidth: Float,
    candleSpacing: Float
) {
    val formatter = SimpleDateFormat("MM/dd", Locale.getDefault())
    val fontSize = 24f

    klines.forEachIndexed { index, kline ->
        val xPos = index * (candleWidth + candleSpacing)
        val labelText = formatter.format(Date(kline.openTime))

        drawContext.canvas.nativeCanvas.apply {
            val matrix = Matrix().apply {
                setRotate(-90f, xPos, size.height - 20f)
            }
            val paint = Paint().apply {
                color = Color.Gray.toArgb()
                textSize = fontSize
                textAlign = Paint.Align.CENTER
            }
            drawText(
                labelText,
                xPos,
                size.height - 20f,
                paint
            )
            drawContext.canvas.nativeCanvas.concat(matrix)
        }
    }
}

private fun Dp.toPx(density: Float): Float = this.value * density









