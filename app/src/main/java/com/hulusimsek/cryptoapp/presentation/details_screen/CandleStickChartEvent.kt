package com.hulusimsek.cryptoapp.presentation.details_screen

import androidx.compose.ui.geometry.Offset
import com.hulusimsek.cryptoapp.domain.model.KlineModel

sealed class CandleStickChartEvent {
    data class OnLongPress(
        val offset: Offset,
        val candleWidthPx: Float,
        val candleSpacingPx: Float
    ) : CandleStickChartEvent()

    data class UpdateKlines(val newKlines: List<KlineModel>) : CandleStickChartEvent()
    data class OnDrag(
        val dragAmount: Offset,
        val candleWidthPx: Float,
        val candleSpacingPx: Float,
        val canvasWidthPx: Float,
        val canvasHeightPx: Float
    ) : CandleStickChartEvent()

    object OnTap : CandleStickChartEvent()
}