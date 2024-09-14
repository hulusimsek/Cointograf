package com.hulusimsek.cryptoapp.data

import android.content.Context
import android.util.Log
import com.hulusimsek.cryptoapp.data.remote.dto.Ticker
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

class WebSocketClient @Inject constructor(
) {

    private val _priceUpdates = MutableStateFlow<Map<String, Ticker>>(emptyMap())
    val priceUpdates: StateFlow<Map<String, Ticker>> = _priceUpdates

    private val client = OkHttpClient()
    private val webSockets = mutableMapOf<String, WebSocket>()

    fun connect(symbols: List<String>) {
        symbols.forEach { symbol ->
            val url = "wss://stream.binance.com:9443/ws/${symbol.toLowerCase()}@ticker"
            Log.d("WebSocket", "Attempting to connect to $url")  // Bağlantı başlıyor

            if (!webSockets.containsKey(url)) {
                val request = Request.Builder().url(url).build()
                val webSocket = client.newWebSocket(request, object : WebSocketListener() {

                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        Log.d("WebSocket", "Connection opened for $url")  // Bağlantı açıldığında
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        Log.d("WebSocket", "Received message for $url: $text")  // Mesaj geldiğinde
                        val ticker = parseTicker(text)
                        _priceUpdates.value = _priceUpdates.value.toMutableMap().apply {
                            put(ticker.symbol, ticker)
                        }
                        Log.d("WebSocket", "Ticker updated: ${ticker.symbol}")  // Ticker güncellendi
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        Log.d("WebSocket", "Connection closed: $reason for $url")
                        super.onClosed(webSocket, code, reason)
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Log.e("WebSocket", "Connection failed for $url: ${t.message}")
                        super.onFailure(webSocket, t, response)
                    }
                })

                webSockets[url] = webSocket
            } else {
                Log.d("WebSocket", "Already connected to $url")
            }
        }
    }


    private fun parseTicker(json: String): Ticker {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing JSON: ${e.message}")
            Ticker(
                eventType = "",
                eventTime = 0L,
                symbol = "",
                priceChange = "",
                priceChangePercent = "",
                weightedAvgPrice = "",
                prevClosePrice = "",
                closePrice = "",
                closeTradeQty = "",
                bestBidPrice = "",
                bestBidQty = "",
                bestAskPrice = "",
                bestAskQty = "",
                openPrice = "",
                highPrice = "",
                lowPrice = "",
                totalVolume = "",
                totalQuoteVolume = "",
                openTime = 0L,
                closeTime = 0L,
                firstTradeId = 0L,
                lastTradeId = 0L,
                tradeCount = 0
            )
        }
    }


    fun disconnect() {
        webSockets.values.forEach { webSocket ->
            webSocket.close(1000, "Disconnecting")
        }
        webSockets.clear()
    }
}
