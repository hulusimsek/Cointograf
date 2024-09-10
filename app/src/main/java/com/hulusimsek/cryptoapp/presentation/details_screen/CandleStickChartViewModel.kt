package com.hulusimsek.cryptoapp.presentation.details_screen

// CandleStickChartViewModel.kt
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.hulusimsek.cryptoapp.domain.model.KlineModel
import kotlin.math.min

class CandleStickChartViewModel : ViewModel() {
    var offsetX by mutableStateOf(0f)
    var offsetY by mutableStateOf(0f)

    var selectedCandleIndex by mutableStateOf<Int?>(null)
    var showPopup by mutableStateOf(false)
    var popupOffset by mutableStateOf(Offset.Zero)

    var guideLineX by mutableStateOf<Float?>(null)
    var guideLineY by mutableStateOf<Float?>(null)
    var isGuideLine by mutableStateOf(false)

    var klines by mutableStateOf<List<KlineModel>>(emptyList())

    fun onDrag(dragAmount: Offset, candleWidthPx: Float, candleSpacingPx: Float, canvasWidthPx: Float, canvasHeightPx: Float) {
        // Aralık sınırlarını doğru hesapla
        val maxOffsetX = -((candleWidthPx + candleSpacingPx) * klines.size - canvasWidthPx)
        val maxOffsetY = -((canvasHeightPx + candleSpacingPx) * klines.size - canvasHeightPx)

        // Offset değerlerini aralık içinde kontrol et
        offsetX = (offsetX + dragAmount.x).checkRange(maxOffsetX, 0f)
        offsetY = (offsetY + dragAmount.y).checkRange(maxOffsetY, 0f)

        if (!isGuideLine) {
            guideLineX = null
            guideLineY = null
        }
    }

    fun onLongPress(offset: Offset, candleWidthPx: Float, candleSpacingPx: Float) {
        if (!isGuideLine) {
            val transformedX = (offset.x - offsetX)
            val candleIndex = (transformedX / (candleWidthPx + candleSpacingPx)).toInt().coerceIn(0, klines.size - 1)

            if (candleIndex in klines.indices) {
                selectedCandleIndex = candleIndex
                showPopup = true

                val candleCenterX = candleIndex * (candleWidthPx + candleSpacingPx) + (candleWidthPx / 2)
                guideLineX = candleCenterX + offsetX
                guideLineY = offset.y
                isGuideLine = true
            }
        }
    }

    fun onTap() {
        showPopup = false
        guideLineX = null
        guideLineY = null
        isGuideLine = false
    }

    fun updateKlines(newKlines: List<KlineModel>) {
        klines = newKlines
        // Adjust offsetX to fit new data
    }

    private fun Float.checkRange(min: Float, max: Float): Float {
        return if (min > max) {
            // Eğer minimum değer maksimumdan büyükse, aralık geçersiz
            this
        } else {
            this.coerceIn(min, max)
        }
    }}


