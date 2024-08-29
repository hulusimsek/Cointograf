package com.hulusimsek.cryptoapp.presentation.details_screen.views

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas // Import this

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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Popup
import com.hulusimsek.cryptoapp.util.Constants.removeTrailingZeros
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CandleStickChartView(
    klines: List<KlineModel>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current.density
    val density2 = LocalDensity.current

    var guideLineX by remember { mutableStateOf<Float?>(null) }
    var guideLineY by remember { mutableStateOf<Float?>(null) }
    var isGuideLine by remember { mutableStateOf(false) }

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

        // Calculate new boundaries
        val minX = -((candleWidthPx * scale + candleSpacingPx) * totalCandles - canvasWidthPx).coerceAtMost(0f)
        val minY = -((canvasHeightPx * scale + candleSpacingPx) * totalCandles - canvasHeightPx).coerceAtMost(0f)

        offsetX = (offsetX + offsetChange.x * scaleChange).checkRange(minX, 0f)
        offsetY = (offsetY + offsetChange.y * scaleChange).checkRange(minY, 0f)

        // Update guide lines if active
        if (isGuideLine) {
            guideLineX = guideLineX?.let { (it - offsetX) * scaleChange + offsetX }
            guideLineY = guideLineY?.let { (it - offsetY) * scaleChange + offsetY }
        }
    }

    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()

            val minX = -((candleWidthPx * scale + candleSpacingPx) * totalCandles - canvasWidthPx).coerceAtMost(0f)
            val minY = -((canvasHeightPx * scale + candleSpacingPx) * totalCandles - canvasHeightPx).coerceAtMost(0f)

            offsetX = (offsetX + dragAmount.x).checkRange(minX, 0f)
            offsetY = (offsetY + dragAmount.y).checkRange(minY, 0f)

            if (!isGuideLine) {
                guideLineX = null
                guideLineY = null
            }
        }
    }
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { offset ->
                    if (!isGuideLine) {
                        val transformedX = (offset.x - offsetX) / scale
                        val candleIndex = (transformedX / (candleWidthPx + candleSpacingPx)).toInt().coerceIn(0, totalCandles - 1)

                        if (candleIndex in klines.indices) {
                            selectedCandleIndex = candleIndex
                            showPopup = true

                            val candleCenterX = candleIndex * (candleWidthPx + candleSpacingPx) + (candleWidthPx / 2)
                            guideLineX = candleCenterX * scale + offsetX
                            guideLineY = offset.y
                            isGuideLine = true
                        }
                    }
                },
                onTap = {
                    showPopup = false
                    guideLineX = null
                    guideLineY = null
                    isGuideLine = false
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    if (isGuideLine) {
                        guideLineX = offset.x
                        guideLineY = offset.y
                    }
                },
                onDrag = { change, dragAmount ->
                    change.consume()

                    if (isGuideLine) {
                        guideLineX = (guideLineX!! + dragAmount.x).coerceIn(0f, canvasWidthPx)
                        guideLineY = (guideLineY!! + dragAmount.y).coerceIn(0f, canvasHeightPx)
                    }
                },
                onDragEnd = {
                    if (isGuideLine) {
                        selectedCandleIndex?.let { index ->
                            val candleCenterX = index * (candleWidthPx + candleSpacingPx) + (candleWidthPx / 2)
                            guideLineX = candleCenterX * scale + offsetX
                            guideLineY = canvasHeightPx / 2f
                        }
                    }
                }
            )
        }

    LaunchedEffect(scale, klines) {
        val canvasWidth = with(density2) { 300.dp.toPx() }
        val totalWidth = (candleWidthPx * scale + candleSpacingPx) * totalCandles - candleSpacingPx
        offsetX = (-totalWidth + canvasWidth).checkRange(-totalWidth, canvasWidth)
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray)
            .clip(RoundedCornerShape(12.dp))
            .then(dragModifier)
            .transformable(state = transformableState)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val totalWidth =
                (candleWidthPx * scale + candleSpacingPx) * totalCandles - candleSpacingPx

            val fromIndex =
                ((-offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt().coerceAtLeast(0)
            val toIndex =
                ((canvasWidth - offsetX) / (candleWidthPx * scale + candleSpacingPx)).toInt() + fromIndex

            val adjustedFromIndex = fromIndex.coerceAtLeast(0)
            val adjustedToIndex = minOf(
                adjustedFromIndex + (canvasWidth / (candleWidthPx * scale + candleSpacingPx)).toInt() + 1,
                totalCandles
            )

            if (adjustedFromIndex < adjustedToIndex && adjustedFromIndex < totalCandles) {
                val visibleKlines = klines.subList(adjustedFromIndex, adjustedToIndex)

                val minLow =
                    visibleKlines.minOfOrNull { it.lowPrice.toFloatOrNull() ?: Float.MAX_VALUE }
                        ?: 0f
                val maxHigh =
                    visibleKlines.maxOfOrNull { it.highPrice.toFloatOrNull() ?: Float.MIN_VALUE }
                        ?: 1f
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

                    // Draw guide lines if active
                    if (isGuideLine) {
                        guideLineX?.let { x ->
                            drawLine(
                                color = Color.Blue,
                                start = Offset(x, 0f),
                                end = Offset(x, canvasHeight),
                                strokeWidth = 1.dp.toPx(density)
                            )
                        }
                        guideLineY?.let { y ->
                            drawLine(
                                color = Color.Blue,
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1.dp.toPx(density)
                            )
                        }
                    }
                }
            }
        }

        if (showPopup && selectedCandleIndex != null) {
            val candleData = klines[selectedCandleIndex!!]
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(popupOffset.x.toInt(), popupOffset.y.toInt())
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Date: ${candleData.openTime}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Open: ${candleData.openPrice}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Close: ${candleData.closePrice}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "High: ${candleData.highPrice}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Low: ${candleData.lowPrice}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// Extension function to convert Dp to Px
private fun Dp.toPx(density: Float) = value * density

// Helper function to clamp values within a given range
private fun Float.checkRange(min: Float, max: Float): Float = coerceIn(min, max)

// Draw candles function
private fun DrawScope.drawCandles(
    klines: List<KlineModel>,
    candleWidth: Float,
    candleSpacing: Float,
    canvasHeight: Float,
    minLow: Float,
    priceRange: Float
) {
    klines.forEachIndexed { index, kline ->
        val openPrice = kline.openPrice.toFloat()
        val closePrice = kline.closePrice.toFloat()
        val highPrice = kline.highPrice.toFloat()
        val lowPrice = kline.lowPrice.toFloat()

        val x = index * (candleWidth + candleSpacing)
        val rectColor = if (closePrice > openPrice) Color.Green else Color.Red

        val candleTop = canvasHeight * (1f - (max(openPrice, closePrice) - minLow) / priceRange)
        val candleBottom = canvasHeight * (1f - (min(openPrice, closePrice) - minLow) / priceRange)

        val highY = canvasHeight * (1f - (highPrice - minLow) / priceRange)
        val lowY = canvasHeight * (1f - (lowPrice - minLow) / priceRange)

        drawRect(
            color = rectColor,
            topLeft = Offset(x, candleTop),
            size = Size(candleWidth, candleBottom - candleTop)
        )
        drawLine(
            color = rectColor,
            start = Offset(x + candleWidth / 2, highY),
            end = Offset(x + candleWidth / 2, lowY),
            strokeWidth = 1.dp.toPx(density)
        )
    }
}

// Draw Y-axis labels
private fun DrawScope.drawYLabels(
    minLow: Float,
    maxHigh: Float,
    priceRange: Float,
    canvasHeight: Float
) {
    val steps = 5
    val step = priceRange / steps

    // Create a native Android graphics Paint
    val labelPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 12.sp.toPx() // Set text size using Compose's text size
        isAntiAlias = true // For smoother text
    }

    (0..steps).forEach { i ->
        val price = minLow + i * step
        val y = canvasHeight * (1f - (price - minLow) / priceRange)

        // Draw text using native canvas
        drawContext.canvas.nativeCanvas.drawText(
            String.format("%.2f", price),
            4.dp.toPx(), // X offset for text drawing
            y,
            labelPaint
        )
    }
}