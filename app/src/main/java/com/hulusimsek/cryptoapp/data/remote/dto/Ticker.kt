package com.hulusimsek.cryptoapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ticker(
    @SerialName("e")
    val eventType: String,    // Event type (JSON'da "e")

    @SerialName("E")
    val eventTime: Long,      // Event time (JSON'da "E")

    @SerialName("s")
    val symbol: String,       // Symbol (JSON'da "s")

    @SerialName("p")
    val priceChange: String,  // Price change (JSON'da "p")

    @SerialName("P")
    val priceChangePercent: String,  // Price change percent (JSON'da "P")

    @SerialName("w")
    val weightedAvgPrice: String,    // Weighted average price (JSON'da "w")

    @SerialName("x")
    val prevClosePrice: String,      // Previous day's close price (JSON'da "x")

    @SerialName("c")
    val closePrice: String,          // Current day's close price (JSON'da "c")

    @SerialName("Q")
    val closeTradeQty: String,       // Close trade quantity (JSON'da "Q")

    @SerialName("b")
    val bestBidPrice: String,        // Best bid price (JSON'da "b")

    @SerialName("B")
    val bestBidQty: String,          // Best bid quantity (JSON'da "B")

    @SerialName("a")
    val bestAskPrice: String,        // Best ask price (JSON'da "a")

    @SerialName("A")
    val bestAskQty: String,          // Best ask quantity (JSON'da "A")

    @SerialName("o")
    val openPrice: String,           // Open price (JSON'da "o")

    @SerialName("h")
    val highPrice: String,           // High price (JSON'da "h")

    @SerialName("l")
    val lowPrice: String,            // Low price (JSON'da "l")

    @SerialName("v")
    val totalVolume: String,         // Total volume (JSON'da "v")

    @SerialName("q")
    val totalQuoteVolume: String,    // Total quote volume (JSON'da "q")

    @SerialName("O")
    val openTime: Long,              // Open time (JSON'da "O")

    @SerialName("C")
    val closeTime: Long,             // Close time (JSON'da "C")

    @SerialName("F")
    val firstTradeId: Long,          // First trade ID (JSON'da "F")

    @SerialName("L")
    val lastTradeId: Long,           // Last trade ID (JSON'da "L")

    @SerialName("n")
    val tradeCount: Int              // Number of trades (JSON'da "n")
)

