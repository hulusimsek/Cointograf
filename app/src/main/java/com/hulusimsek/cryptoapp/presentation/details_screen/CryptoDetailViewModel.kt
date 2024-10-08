package com.hulusimsek.cryptoapp.presentation.details_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.data.WebSocketClient
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.data.remote.dto.Ticker
import com.hulusimsek.cryptoapp.domain.use_case.get_crypto_details_use_case.GetCryptoDetailsUseCase
import com.hulusimsek.cryptoapp.domain.use_case.get_klines_use_case.GetKlinesUseCase
import com.hulusimsek.cryptoapp.presentation.home_screen.CryptosEvent
import com.hulusimsek.cryptoapp.util.Constants.removeTrailingZeros
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val getCryptoDetailsUseCase: GetCryptoDetailsUseCase,
    private val getKlinesUseCase: GetKlinesUseCase,
    val webSocketClient: WebSocketClient
) : ViewModel() {

    private val _state = MutableStateFlow<CoinDetailState>(CoinDetailState())
    val state: StateFlow<CoinDetailState> = _state

    private val _interval = MutableStateFlow<String>("1h")
    val interval: StateFlow<String> = _interval

    private var job: Job? = null
    private var job2: Job? = null

    private fun updateData(updates: Map<String, Ticker>) {
        val currentCoin = _state.value.coin

        // Eğer coin null ise, güncelleme yapılmayacak
        if (currentCoin != null) {
            val ticker = updates[currentCoin.symbol]

            // Eğer updates'ten gelen ticker null değilse, güncelleme yapılıyor
            if (ticker != null) {
                val priceChangePercent = ticker.priceChangePercent.toFloatOrNull() ?: 0f
                val formattedPriceChangePercent = String.format("%.2f", priceChangePercent)

                _state.value = _state.value.copy(
                    coin = currentCoin.copy(
                        priceChangePercent = formattedPriceChangePercent,
                        lastPrice = removeTrailingZeros(ticker.closePrice),
                        quoteVolume = ticker.totalQuoteVolume
                    )
                )
            } else {
                // Eğer ticker null ise, loglama yapabiliriz
                Log.e("websocket", "Ticker not found for symbol: ${currentCoin.symbol}")
            }
        } else {
            // Eğer coin null ise, loglama yapabiliriz
            Log.e("websocket", "Current coin is null in the state")
        }
    }


    fun updateSelectedSymbols(symbols: List<String>) {
        webSocketClient.connect(symbols) // WebSocket bağlantılarını güncelle
    }

    private fun startWebSocketUpdates() {
        viewModelScope.launch {
            webSocketClient.priceUpdates.collect { updates ->
                updateData(updates)  // Tab verilerini güncelle
            }
        }
    }


    private fun selectTimeRange(timeRange: String) {
        _interval.value = timeRange
        fetchKlinesData()
    }

    private fun refresh() {
        val cryptoSymbol = _state.value.currentCrypto
        if (cryptoSymbol != null) {
            loadCryptoDetails(cryptoSymbol)
            if (_state.value.error.isNullOrEmpty() && _state.value.isLoading) {
                _state.value.copy(
                    toastMessage = "Veriler güncellendi.",
                )
            }
        } else {
            _state.value = _state.value.copy(
                toastMessage = "Güncellenecek veri bulunamadı.",
                isLoading = false
            )
        }
    }

    private fun loadCryptoDetails(cryptoSymbol: String) {

        _state.value = _state.value.copy(currentCrypto = cryptoSymbol)
        job?.cancel()


        job = getCryptoDetailsUseCase.executeGetCryptos(cryptoSymbol).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = CoinDetailState(
                        coin = it.data,
                        currentCrypto = cryptoSymbol
                    )
                    fetchKlinesData()
                    startWebSocketUpdates()
                }

                is Resource.Error -> {
                    _state.value =
                        CoinDetailState(error = it.message ?: "Error!", toastMessage = it.message)
                }

                is Resource.Loading -> {
                    _state.value = CoinDetailState(isLoading = true)
                }
            }


        }.launchIn(viewModelScope)

    }

    private fun clearToastMessage() {
        _state.value = _state.value.copy(
            toastMessage = null,
        )
    }

    private fun fetchKlinesData() {
        job2?.cancel()


        job2 = getKlinesUseCase.executeGetKlines(_state.value.currentCrypto ?: "", _interval.value)
            .onEach {
                when (it) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            klines = it.data,
                            isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            klinesError = it.message ?: "Error!",
                            toastMessage = it.message,
                            isLoading = false
                        )

                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }


            }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.disconnect() // ViewModel temizlenirken WebSocket bağlantılarını kes
    }

    fun onEvent(event: CryptoDetailsEvent) {
        when (event) {
            is CryptoDetailsEvent.LoadCryptoDetails -> loadCryptoDetails(event.symbol)
            is CryptoDetailsEvent.Refresh -> refresh()
            is CryptoDetailsEvent.ClearToastMessage -> clearToastMessage()
            is CryptoDetailsEvent.SelectTimeRange -> selectTimeRange(event.timeRange)
        }
    }
}