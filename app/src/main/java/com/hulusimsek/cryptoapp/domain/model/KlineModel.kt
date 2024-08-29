package com.hulusimsek.cryptoapp.domain.model

data class KlineModel(
    val index: Int,
    val openTime: Long, // Kline open time
    val openPrice: String, // Open price
    val highPrice: String, // High price
    val lowPrice: String, // Low price
    val closePrice: String, // Close price
    val volume: String, // Volume
    val closeTime: Long, // Kline Close time
    val quoteAssetVolume: String, // Quote asset volume
    val numberOfTrades: Int, // Number of trades
    val takerBuyBaseAssetVolume: String, // Taker buy base asset volume
    val takerBuyQuoteAssetVolume: String, // Taker buy quote asset volume
    val ignore: String // Unused field, ignore.
)