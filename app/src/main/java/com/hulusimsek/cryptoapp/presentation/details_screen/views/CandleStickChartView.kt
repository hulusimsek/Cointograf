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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.unit.Density
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun CandleStickChartView(
    klines: List<KlineModel>,
    modifier: Modifier = Modifier
) {
    // Zoom, offset ve rotation durumlarını tanımla
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = (scale * zoomChange).coerceIn(0.5f, 2.0f)
        val scaleChange = newScale / scale
        scale = newScale

        offsetX += offsetChange.x * scaleChange
        offsetY += offsetChange.y * scaleChange

    }

    val density = LocalDensity.current.density
    val density2 = LocalDensity.current

    val candleWidthDp = 8.dp
    val candleSpacingDp = 4.dp
    val candleWidthPx = candleWidthDp.toPx(density)
    val candleSpacingPx = candleSpacingDp.toPx(density)
    val totalCandles = klines.size

    // Başlangıçta son mumun görünmesini sağlamak için offsetX hesaplama
    LaunchedEffect(klines) {
        val canvasWidth = with(density2) { 300.dp.toPx() }
        val totalWidth = (candleWidthPx * scale + candleSpacingPx) * totalCandles - candleSpacingPx
        offsetX = -(totalWidth - canvasWidth).coerceAtLeast(0f)
    }

    Box(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .transformable(state = state)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Toplam genişliği hesapla
            val totalWidth = (candleWidthPx * scale + candleSpacingPx) * totalCandles - candleSpacingPx

            // Görüntülenebilir mumların sınırlarını belirle
            val fromIndex = ((-offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt().coerceAtLeast(0)
            val toIndex = ((canvasWidth - offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt() + fromIndex

            // Ekranda görünmesi gereken mumları belirle
            val adjustedFromIndex = fromIndex.coerceAtLeast(0)
            val adjustedToIndex = minOf(adjustedFromIndex + (canvasWidth / (candleWidthPx * scale + candleSpacingPx)).toInt() + 1, totalCandles)

            if (adjustedFromIndex < adjustedToIndex && adjustedFromIndex < totalCandles) {
                // Görüntülenen mumları al
                val visibleKlines = klines.subList(adjustedFromIndex, adjustedToIndex)

                val minLow = visibleKlines.minOfOrNull { it.lowPrice.toFloatOrNull() ?: Float.MAX_VALUE } ?: 0f
                val maxHigh = visibleKlines.maxOfOrNull { it.highPrice.toFloatOrNull() ?: Float.MIN_VALUE } ?: 1f
                val priceRange = maxHigh - minLow

                // Mumları çiz
                drawCandles(
                    visibleKlines,
                    candleWidthPx * scale,
                    candleSpacingPx,
                    canvasHeight,
                    minLow,
                    priceRange
                )

                // Y eksenini güncelle
                drawYLabels(minLow, maxHigh, priceRange, canvasHeight)

                // X eksenini güncelle
                drawXLabels(visibleKlines, canvasWidth, candleWidthPx * scale, candleSpacingPx)
            }
        }
    }
}

// Mumlar çizim fonksiyonu
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

        // Mum gövdesi
        drawRect(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            topLeft = Offset(candleX, minOf(candleYOpen, candleYClose)),
            size = Size(candleWidth, Math.abs(candleYOpen - candleYClose))
        )

        // Mum fitili
        drawLine(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            start = Offset(candleX + candleWidth / 2, candleYHigh),
            end = Offset(candleX + candleWidth / 2, candleYLow),
            strokeWidth = 2f
        )
    }
}

// Y ekseni etiketleri çizimi
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

// X ekseni etiketleri çizimi
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

// DP to PX dönüştürme fonksiyonu
private fun Dp.toPx(density: Float): Float = this.value * density

























