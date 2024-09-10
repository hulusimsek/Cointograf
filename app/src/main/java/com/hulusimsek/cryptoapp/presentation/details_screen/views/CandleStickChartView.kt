
package com.hulusimsek.cryptoapp.presentation.details_screen.views

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan
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

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()


    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()

            offsetX = (offsetX + dragAmount.x).checkRange(
                -((candleWidthPx + candleSpacingPx) * totalCandles - canvasWidthPx),
                0f
            )
            offsetY = (offsetY + dragAmount.y).checkRange(
                -((canvasHeightPx + candleSpacingPx) * totalCandles - canvasHeightPx),
                0f
            )

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
                        val transformedX = (offset.x - offsetX)
                        val candleIndex = (transformedX / (candleWidthPx + candleSpacingPx)).toInt().coerceIn(0, totalCandles - 1)

                        if (candleIndex in klines.indices) {
                            selectedCandleIndex = candleIndex
                            showPopup = true

                            val candleCenterX = candleIndex * (candleWidthPx + candleSpacingPx) + (candleWidthPx / 2)
                            guideLineX = candleCenterX + offsetX
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

    LaunchedEffect(klines) {
        val canvasWidth = with(density2) { 300.dp.toPx() }
        val totalWidth = (candleWidthPx + candleSpacingPx) * totalCandles - candleSpacingPx
        offsetX = (-totalWidth + canvasWidth).checkRange(-totalWidth, canvasWidth)
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .then(dragModifier)
    ) {
        // 1. Bölüm: Mum Grafiği
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val totalWidth =
                    (candleWidthPx + candleSpacingPx) * totalCandles - candleSpacingPx

                val fromIndex =
                    ((-offsetX) / (candleWidthPx + candleSpacingPx)).toInt().coerceAtLeast(0)
                val toIndex =
                    ((canvasWidth - offsetX) / (candleWidthPx + candleSpacingPx)).toInt() + fromIndex

                val adjustedFromIndex = fromIndex.coerceAtLeast(0)
                val adjustedToIndex = minOf(
                    adjustedFromIndex + (canvasWidth / (candleWidthPx + candleSpacingPx)).toInt() + 1,
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
                        // Mum çubuklarını çizme
                        drawCandles(
                            klines = visibleKlines,
                            candleWidth = candleWidthPx,
                            candleSpacing = candleSpacingPx,
                            canvasHeight = canvasHeight,
                            minLow = minLow,
                            priceRange = priceRange
                        )

                        drawYLabels(
                            minLow = minLow,
                            maxHigh = maxHigh,
                            priceRange = priceRange,
                            canvasHeight = canvasHeight,
                            textColor = textColor
                        )

                        // Kılavuz çizgilerini ölçeklenmiş ve kaydırılmış konumlarla çiz
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
        }

        // 2. Bölüm: Hacim Grafiği
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val totalWidth =
                    (candleWidthPx + candleSpacingPx) * totalCandles - candleSpacingPx

                val fromIndex =
                    ((-offsetX) / (candleWidthPx + candleSpacingPx)).toInt().coerceAtLeast(0)
                val toIndex =
                    ((canvasWidth - offsetX) / (candleWidthPx + candleSpacingPx)).toInt() + fromIndex

                val adjustedFromIndex = fromIndex.coerceAtLeast(0)
                val adjustedToIndex = minOf(
                    adjustedFromIndex + (canvasWidth / (candleWidthPx + candleSpacingPx)).toInt() + 1,
                    totalCandles
                )

                if (adjustedFromIndex < adjustedToIndex && adjustedFromIndex < totalCandles) {
                    val visibleKlines = klines.subList(adjustedFromIndex, adjustedToIndex)

                    // Hacim grafiği çizimi
                    drawVolumes(
                        klines = visibleKlines,
                        candleWidth = candleWidthPx,
                        candleSpacing = candleSpacingPx,
                        canvasHeight = canvasHeight
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val canvasWidth = size.width
                val padding = 40.dp.toPx() // Baştaki ve sondaki tarihler için padding ekledik

                // Başta, ortada ve sonda tarihler için endeks hesaplaması
                val fromIndex =
                    ((-offsetX) / (candleWidthPx + candleSpacingPx)).toInt().coerceAtLeast(0)
                val toIndex =
                    ((canvasWidth - offsetX) / (candleWidthPx + candleSpacingPx)).toInt() + fromIndex

                val adjustedFromIndex = fromIndex.coerceAtLeast(0)
                val adjustedToIndex = minOf(
                    adjustedFromIndex + (canvasWidth / (candleWidthPx + candleSpacingPx)).toInt() + 1,
                    totalCandles
                )

                if (adjustedFromIndex < adjustedToIndex && adjustedFromIndex < totalCandles) {
                    val visibleKlines = klines.subList(adjustedFromIndex, adjustedToIndex)

                    // Başta, ortada ve sonda olan tarihler
                    val firstKline = visibleKlines.first()
                    val middleKline = visibleKlines[visibleKlines.size / 2]
                    val lastKline = visibleKlines.last()

                    val firstDateX = padding // Baştaki tarih padding ile yerleştiriliyor
                    val middleDateX = canvasWidth / 2
                    val lastDateX = canvasWidth - padding

                    // Tarih formatlama
                    val firstDate = formatDate(firstKline.openTime)
                    val middleDate = formatDate(middleKline.openTime)
                    val lastDate = formatDate(lastKline.openTime)



                    // Başta, ortada ve sondaki tarihleri çizme
                    drawContext.canvas.nativeCanvas.drawText(
                        firstDate,
                        firstDateX,
                        30f,  // Y ekseninde tarihin konumu
                        Paint().apply {
                            color = textColor
                            textSize = 30f
                            textAlign = Paint.Align.CENTER
                        }
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        middleDate,
                        middleDateX,
                        30f,
                        Paint().apply {
                            color = textColor
                            textSize = 30f
                            textAlign = Paint.Align.CENTER
                        }
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        lastDate,
                        lastDateX,
                        30f,
                        Paint().apply {
                            color = textColor
                            textSize = 30f
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }




        // Seçili mum çubuğu için popup gösterme
        if (showPopup && selectedCandleIndex != null) {
            val candle = klines[selectedCandleIndex!!]
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(popupOffset.x.toInt(), popupOffset.y.toInt())
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Gray)
                        .padding(8.dp)
                ) {
                    Text("Açılış: ${removeTrailingZeros(candle.openPrice)}", fontSize = 14.sp)
                    Text("Kapanış: ${removeTrailingZeros(candle.closePrice)}", fontSize = 14.sp)
                    Text("En Yüksek: ${removeTrailingZeros(candle.highPrice)}", fontSize = 14.sp)
                    Text("En Düşük: ${removeTrailingZeros(candle.lowPrice)}", fontSize = 14.sp)
                    Text("Tarih: ${formatDate(candle.openTime)}", fontSize = 14.sp)
                }
            }
        }
    }
}


// Hacim grafiği çizen fonksiyon
private fun DrawScope.drawVolumes(
    klines: List<KlineModel>,
    candleWidth: Float,
    candleSpacing: Float,
    canvasHeight: Float
) {
    val maxVolume = klines.maxOfOrNull { it.volume.toFloatOrNull() ?: 0f } ?: 1f
    val volumeHeightRatio = canvasHeight / maxVolume

    klines.forEachIndexed { index, kline ->
        val volume = kline.volume.toFloatOrNull() ?: 0f
        val volumeHeight = volume * volumeHeightRatio

        val candleX = index * (candleWidth + candleSpacing)
        val candleColor = if ((kline.closePrice.toFloatOrNull() ?: 0f) >= (kline.openPrice.toFloatOrNull() ?: 0f)) {
            Color.Green
        } else {
            Color.Red
        }

        drawRect(
            color = candleColor,
            topLeft = Offset(candleX, canvasHeight - volumeHeight),
            size = Size(candleWidth, volumeHeight)
        )
    }
}







fun Float.checkRange(min: Float, max: Float): Float {
    return coerceIn(min, max)
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

        // Candle body
        drawRect(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            topLeft = Offset(candleX, minOf(candleYOpen, candleYClose)),
            size = Size(candleWidth, Math.abs(candleYOpen - candleYClose))
        )

        // Candle wick
        drawLine(
            color = if (closePrice >= openPrice) Color.Green else Color.Red,
            start = Offset(candleX + candleWidth / 2, candleYHigh),
            end = Offset(candleX + candleWidth / 2, candleYLow),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawYLabels(
    minLow: Float,
    maxHigh: Float,
    priceRange: Float,
    canvasHeight: Float,
    textColor: Int
) {
    val labelCount = 9

    for (i in 0 until labelCount) {
        val labelValue = minLow + (priceRange / (labelCount - 1)) * i
        val yPos = canvasHeight - ((labelValue - minLow) * (canvasHeight / priceRange))
        val labelText = removeTrailingZeros(labelValue.toString())

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                labelText,
                20f,
                yPos,
                Paint().apply {
                    color = textColor
                    textSize = 24f
                    textAlign = Paint.Align.LEFT
                }
            )
        }
    }
}

private fun Dp.toPx(density: Float): Float = this.value * density


fun formatDate(epochMillis: Long): String {
    val date = Date(epochMillis)
    val format = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    return format.format(date)
}