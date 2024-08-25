package com.hulusimsek.cryptoapp.domain.model

data class Candle(
    val time: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)