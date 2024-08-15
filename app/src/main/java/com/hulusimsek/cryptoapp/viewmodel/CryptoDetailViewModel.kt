package com.hulusimsek.cryptoapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel(){

    var cryptoItems = mutableStateOf<CryptoItem?>(null)
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)


    fun loadCrpyoto(crypoSymbol : String) {

        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getCrypto(crypoSymbol)

            when(result) {
                is Resource.Success -> {
                    cryptoItems.value = result.data!!

                    errorMessage.value = ""
                    isLoading.value = false
                }

                is Resource.Error -> {
                    errorMessage.value = result.message!!
                    isLoading.value = false
                }
                is Resource.Loading -> {
                    isLoading.value = true
                }
            }
        }
    }
}