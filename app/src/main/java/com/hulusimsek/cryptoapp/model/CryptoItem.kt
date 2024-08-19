package com.hulusimsek.cryptoapp.model

data class CryptoItem(
    val name: String? = null,
    val surname: String? = null,

    val askPrice: String,
    val askQty: String,
    val bidPrice: String,
    val bidQty: String,
    val closeTime: Long,
    val count: Int,
    val firstId: Long,
    val highPrice: String,
    val lastId: Long,
    var lastPrice: String,
    val lastQty: String,
    val lowPrice: String,
    val openPrice: String,
    val openTime: Long,
    val prevClosePrice: String,
    val priceChange: String,
    val priceChangePercent: String,
    val quoteVolume: String,
    val symbol: String,
    val volume: String,
    val weightedAvgPrice: String
)