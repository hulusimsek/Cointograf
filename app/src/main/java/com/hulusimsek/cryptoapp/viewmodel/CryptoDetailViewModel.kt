package com.hulusimsek.cryptoapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel(){
    private val _cryptoItems = MutableStateFlow<CryptoItem?>(null)
    val cryptoItems: StateFlow<CryptoItem?> = _cryptoItems

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _test = MutableStateFlow<String>("umit")
    val test: StateFlow<String> = _test


    private val _currentCrypto = MutableStateFlow<String?>(null)
    val currentCrypto: StateFlow<String?> = _currentCrypto

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun refresh() {
        viewModelScope.launch {
            Log.e("CryptoDetailViewModel", "Refresh started")
            _isLoading.value = true
            _currentCrypto.value?.let {cryptoSymbol ->
                loadCrpyoto(cryptoSymbol)
            }
            _isLoading.value = false
            Log.e("CryptoDetailViewModel", "Refresh ended")
        }
    }

    fun loadCrpyoto(cryptoSymbol : String) {

        viewModelScope.launch {
            _isLoading.value = true
            _currentCrypto.value = cryptoSymbol
            val result = repository.getCrypto(cryptoSymbol)

            when(result) {
                is Resource.Success -> {
                    _cryptoItems.value = result.data!!

                    _errorMessage.value = ""
                    _toastMessage.value = "Veriler başarıyla güncellendi."
                    _isLoading.value = false
                }

                is Resource.Error -> {
                    _errorMessage.value = result.message!!
                    _isLoading.value = false
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}