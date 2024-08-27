package com.hulusimsek.cryptoapp.presentation.details_screen.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun CandleStickChartView(
    klines: List<KlineModel>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val density = LocalDensity.current.density
    val density2 = LocalDensity.current

    val candleWidthDp = 8.dp
    val candleSpacingDp = 4.dp
    val candleWidthPx = candleWidthDp.toPx(density)
    val candleSpacingPx = candleSpacingDp.toPx(density)
    val canvasWidthPx = with(density2) { 300.dp.toPx() }
    val canvasHeightPx = with(density2) { 400.dp.toPx() }

    val totalCandles = klines.size

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(0.5f, 2.0f)
        val scaleChange = newScale / scale
        scale = newScale

        // Yumuşatılmış kaydırma hesaplamaları
        offsetX = (offsetX + offsetChange.x * scaleChange).coerceIn(
            -((candleWidthPx * scale + candleSpacingPx) * totalCandles - canvasWidthPx),
            0f
        )
        offsetY = (offsetY + offsetChange.y * scaleChange).coerceIn(
            -((canvasHeightPx * scale + candleSpacingPx) * totalCandles - canvasHeightPx),
            0f
        )
    }

    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()

            // Tek parmakla kaydırma işlemi
            offsetX = (offsetX + dragAmount.x).coerceIn(
                -((candleWidthPx * scale + candleSpacingPx) * totalCandles - canvasWidthPx),
                0f
            )
            offsetY = (offsetY + dragAmount.y).coerceIn(
                -((canvasHeightPx * scale + candleSpacingPx) * totalCandles - canvasHeightPx),
                0f
            )
        }
    }

    LaunchedEffect(scale, klines) {
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
            .then(dragModifier) // Tek parmak kaydırma için
            .transformable(state = transformableState) // Çift parmak zoom için
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val totalWidth = (candleWidthPx * scale + candleSpacingPx) * totalCandles - candleSpacingPx

            val fromIndex = ((-offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt().coerceAtLeast(0)
            val toIndex = ((canvasWidth - offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt() + fromIndex

            val adjustedFromIndex = fromIndex.coerceAtLeast(0)
            val adjustedToIndex = minOf(adjustedFromIndex + (canvasWidth / (candleWidthPx * scale + candleSpacingPx)).toInt() + 1, totalCandles)

            if (adjustedFromIndex < adjustedToIndex && adjustedFromIndex < totalCandles) {
                val visibleKlines = klines.subList(adjustedFromIndex, adjustedToIndex)

                val minLow = visibleKlines.minOfOrNull { it.lowPrice.toFloatOrNull() ?: Float.MAX_VALUE } ?: 0f
                val maxHigh = visibleKlines.maxOfOrNull { it.highPrice.toFloatOrNull() ?: Float.MIN_VALUE } ?: 1f
                val priceRange = maxHigh - minLow

                if (priceRange > 0) {
                    drawCandles(
                        klines = visibleKlines,
                        candleWidth = candleWidthPx * scale,
                        candleSpacing = candleSpacingPx,
                        canvasHeight = canvasHeight,
                        minLow = minLow,
                        priceRange = priceRange
                    )

                    drawYLabels(
                        minLow = minLow,
                        maxHigh = maxHigh,
                        priceRange = priceRange,
                        canvasHeight = canvasHeight
                    )

                    drawXLabels(
                        klines = visibleKlines,
                        canvasWidth = canvasWidth,
                        candleWidth = candleWidthPx * scale,
                        candleSpacing = candleSpacingPx
                    )
                }
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
    val formatter = DecimalFormat("#,###.################")

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
    val textPaint = Paint().apply {
        color = Color.Gray.toArgb()
        textSize = fontSize
        textAlign = Paint.Align.CENTER
    }

    klines.forEachIndexed { index, kline ->
        val xPos = index * (candleWidth + candleSpacing) + candleWidth / 2
        val labelText = formatter.format(Date(kline.openTime))

        // Y ekseninde bir kaydırma ekleyebilirsiniz
        val yOffset = size.height - 20f

        // Yüzeyi daha iyi konumlandırmak için bu değişiklikleri yapabilirsiniz
        drawContext.canvas.nativeCanvas.apply {
            val matrix = Matrix().apply {
                setRotate(-90f, xPos, yOffset)
            }
            drawText(
                labelText,
                xPos,
                yOffset,
                textPaint
            )
        }
    }
}


// DP to PX dönüştürme fonksiyonu
private fun Dp.toPx(density: Float): Float = this.value * density




























