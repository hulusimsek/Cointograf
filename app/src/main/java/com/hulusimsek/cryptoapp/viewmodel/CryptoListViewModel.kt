package com.hulusimsek.cryptoapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CryptoListViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel() {

    private val _cryptoList = MutableStateFlow<List<CryptoItem>>(listOf())
    val cryptoList: StateFlow<List<CryptoItem>> = _cryptoList

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var initialCryptoList = listOf<CryptoItem>()
    private var isSearchStarting = true

    private val _searchQueryList = MutableStateFlow<List<SearchQuery>>(listOf())
    val searchQueryList: StateFlow<List<SearchQuery>> = _searchQueryList

    init {
        loadCrpyots()
        loadSearchQueries()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            loadCrpyots()
            // Arama sorgusu varsa, arama işlemi yap
            if (_searchQuery.value.isNotEmpty()) {
                searchCryptoList(_searchQuery.value)
            } else {
                // Arama sorgusu boşsa, listeyi başlat
                _cryptoList.value = initialCryptoList
            }
            _isLoading.value = false
        }
    }

    fun loadCrpyots() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCryptoList24hr()

            when (result) {
                is Resource.Success -> {
                    val cryptoItems = result.data!!.mapIndexed { index, item ->
                        CryptoItem(
                            symbol = item.symbol,
                            lastPrice = item.lastPrice,
                            askPrice = item.askPrice,
                            askQty = item.askQty,
                            bidPrice = item.bidPrice,
                            bidQty = item.bidQty,
                            closeTime = item.closeTime,
                            count = item.count,
                            firstId = item.firstId,
                            highPrice = item.highPrice,
                            lastId = item.lastId,
                            lastQty = item.lastQty,
                            lowPrice = item.lowPrice,
                            openPrice = item.openPrice,
                            openTime = item.openTime,
                            prevClosePrice = item.prevClosePrice,
                            priceChange = item.priceChange,
                            priceChangePercent = item.priceChangePercent,
                            quoteVolume = item.quoteVolume,
                            volume = item.volume,
                            weightedAvgPrice = item.weightedAvgPrice
                        )

                    }

                    // Arama başlamışsa, listeyi güncelle
                    if (isSearchStarting) {
                        initialCryptoList = cryptoItems
                        _cryptoList.value = initialCryptoList
                        isSearchStarting = false
                    } else {
                        _cryptoList.value = applySearchFilter(cryptoItems)
                    }

                    _errorMessage.value = ""
                    _isLoading.value = false
                    _toastMessage.value = "Veriler başarıyla güncellendi." // Başarı durumunda mesajı ayarla

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

    private fun applySearchFilter(cryptoItems: List<CryptoItem>): List<CryptoItem> {
        val currentQuery = _searchQuery.value
        return if (currentQuery.isEmpty()) {
            cryptoItems
        } else {
            cryptoItems.filter { it.symbol.contains(currentQuery.trim(), ignoreCase = true) }
        }
    }

    fun searchCryptoList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _searchQuery.value = query
            if (query.isEmpty()) {
                _cryptoList.value = initialCryptoList
                isSearchStarting = true
            } else {
                if (isSearchStarting) {
                    initialCryptoList = _cryptoList.value.toList()
                    isSearchStarting = false
                }

                val result = initialCryptoList.filter {
                    it.symbol.contains(query.trim(), ignoreCase = true)
                }
                _cryptoList.value = result
            }

            // Arama sorgusunu kaydet
        }
    }

    private fun loadSearchQueries() {
        viewModelScope.launch {
            _searchQueryList.value = repository.getSearchQuery()
        }
    }
    fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            repository.deleteSearchQueryByQuery(query)
            repository.insertSearchQuery(SearchQuery(query = query))
            loadSearchQueries()
        }
    }

    fun deleteSearchQuery(query: SearchQuery) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
            loadSearchQueries()
        }
    }


}
