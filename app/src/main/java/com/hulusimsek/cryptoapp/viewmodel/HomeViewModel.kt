package com.hulusimsek.cryptoapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Constants.getBtcSymbols
import com.hulusimsek.cryptoapp.util.Constants.removeTrailingZeros
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface,
    private val context: Context
) : ViewModel() {

    private val _cryptoList = MutableStateFlow<List<CryptoItem>>(listOf())
    val cryptoList: StateFlow<List<CryptoItem>> = _cryptoList

    private val _filterCryptoList = MutableStateFlow<List<CryptoItem>>(listOf())
    val filterCryptoList: StateFlow<List<CryptoItem>> = _filterCryptoList


    private val _tab0 = MutableStateFlow<List<CryptoItem>>(listOf())
    val tab0: StateFlow<List<CryptoItem>> = _tab0

    private val _tab1 = MutableStateFlow<List<CryptoItem>>(listOf())
    val tab1: StateFlow<List<CryptoItem>> = _tab1

    private val _tab2 = MutableStateFlow<List<CryptoItem>>(listOf())
    val tab2: StateFlow<List<CryptoItem>> = _tab2

    private val _tab3 = MutableStateFlow<List<CryptoItem>>(listOf())
    val tab3: StateFlow<List<CryptoItem>> = _tab3

    private val _tab4 = MutableStateFlow<List<CryptoItem>>(listOf())
    val tab4: StateFlow<List<CryptoItem>> = _tab4




    private val _selectedSymbol = MutableStateFlow<String?>("USDT")
    val selectedSymbol: StateFlow<String?> = _selectedSymbol


    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex


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

    fun String.toDoubleOrZero(): Double = this.toDoubleOrNull() ?: 0.0

    fun selectSymbol(symbol: String) {
        _selectedSymbol.value = symbol
    }

    fun selectTab(tabIndex: Int) {
        _selectedTabIndex.value = tabIndex
        val filteredList =
            if (_selectedSymbol.value.isNullOrEmpty() || _selectedSymbol.value == context.getString(
                    R.string.allMarkets
                )
            ) {
                _cryptoList.value
            } else {
                _cryptoList.value.filter { it.surname == _selectedSymbol.value }
            }

         when (tabIndex) {
            0 -> {
                _tab0.value =filteredList
                    .reversed() // Gelen listeyi ters çevir
                    .take(10)
                //Log.d("TabFilter", "Tab 0 selected - Reversing list")
                _filterCryptoList.value = _tab0.value
            }

            1 -> {
                //Log.d("TabFilter", "Tab 1 selected - Sorting by priceChangePercent descending")
                _tab1.value=filteredList
                    .sortedByDescending { it.priceChangePercent.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                    .take(10)
                    .also { sortedList ->
                        sortedList.forEach { item ->
                            //Log.d("TabFilter", "Sorted Item symbol: ${item.symbol}, priceChangePercent: ${item.priceChangePercent}")
                        }
                    }
                _filterCryptoList.value = _tab1.value
            }

            2 -> {
                //Log.d("TabFilter", "Tab 2 selected - Sorting by priceChangePercent ascending")
                _tab2.value=filteredList
                    .sortedBy { it.priceChangePercent.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                    .take(10)
                    .also { sortedList ->
                        sortedList.forEach { item ->
                            //Log.d("TabFilter", "Sorted Item symbol: ${item.symbol}, priceChangePercent: ${item.priceChangePercent}")
                        }
                    }
                _filterCryptoList.value = _tab2.value
            }

            3 -> {
                //Log.d("TabFilter", "Tab 3 selected - Sorting by lastPrice descending")
                _tab3.value=filteredList
                    .sortedByDescending { it.lastPrice.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                    .take(10)
                _filterCryptoList.value = _tab3.value
            }

            4 -> {
                //Log.d("TabFilter", "Tab 4 selected - Sorting by volume descending")
                _tab4.value=filteredList
                    .sortedByDescending { it.volume.replace(",", ".").toDoubleOrZero() }
                    .take(10)
                _filterCryptoList.value = _tab4.value
            }

            else -> {
                //Log.d("TabFilter", "Unknown tab index selected")
                filteredList
            }
        }





    }


    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadCrpyots()
                selectTab(_selectedTabIndex.value)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCrpyots() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCryptoList24hr()

            when (result) {
                is Resource.Success -> {
                    val cryptoItems = result.data!!.map { item ->
                        // Varsayılan olarak surname ve name ayarla
                        var name = item.symbol
                        var surname = ""

                        // Surname olarak ayırmak istediğimiz listeyi kullanarak ayrıştırma yapıyoruz
                        getBtcSymbols(context = context).findLast { item.symbol.endsWith(it) }
                            ?.let { potentialSurname ->
                                surname = potentialSurname
                                name = item.symbol.removeSuffix(potentialSurname)
                            }


                        val priceChangePercent = item.priceChangePercent.toFloatOrNull() ?: 0f
                        val formattedPriceChangePercent = String.format("%.2f", priceChangePercent)

                        CryptoItem(
                            surname = surname,
                            name = name,
                            symbol = item.symbol,
                            lastPrice = removeTrailingZeros(item.lastPrice),
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
                            priceChangePercent = formattedPriceChangePercent,
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
                    selectTab(_selectedTabIndex.value)

                    _toastMessage.value =
                        "Veriler başarıyla güncellendi." // Başarı durumunda mesajı ayarla

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
