package com.hulusimsek.cryptoapp.presentation.details_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.domain.use_case.get_crypto_details_use_case.GetCryptoDetailsUseCase
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
    private val getCryptoDetailsUseCase: GetCryptoDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CoinDetailState>(CoinDetailState())
    val state: StateFlow<CoinDetailState> = _state

    private var job: Job? = null

    private fun refresh() {
        val cryptoSymbol = _state.value.currentCrypto
        if (cryptoSymbol != null) {
            loadCryptoDetails(cryptoSymbol)
            if(_state.value.error.isNullOrEmpty() && _state.value.isLoading){
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
        job?.cancel()


        job = getCryptoDetailsUseCase.executeGetCryptos(cryptoSymbol).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = CoinDetailState(
                        coin = it.data,
                        currentCrypto = cryptoSymbol
                    )

                }

                is Resource.Error -> {
                    _state.value = CoinDetailState(error = it.message ?: "Error!", toastMessage = it.message)
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

    fun onEvent(event: CryptoDetailsEvent) {
        when (event) {
            is CryptoDetailsEvent.LoadCryptoDetails -> loadCryptoDetails(event.symbol)
            is CryptoDetailsEvent.Refresh -> refresh()
            is CryptoDetailsEvent.ClearToastMessage -> clearToastMessage()
        }
    }
}