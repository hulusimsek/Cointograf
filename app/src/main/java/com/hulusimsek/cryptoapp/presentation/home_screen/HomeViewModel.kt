package com.hulusimsek.cryptoapp.presentation.home_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.domain.use_case.delete_search_query_use_case.DeleteSearchQueryUseCase
import com.hulusimsek.cryptoapp.domain.use_case.get_cryptos.GetCryptosUseCase
import com.hulusimsek.cryptoapp.domain.use_case.load_search_queries_use_case.LoadSearchQueriesUseCase
import com.hulusimsek.cryptoapp.domain.use_case.save_search_query_use_case.SaveSearchQueryUseCase
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCryptosUseCase: GetCryptosUseCase,
    private val context: Context,
    private val loadSearchQueriesUseCase: LoadSearchQueriesUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase

) : ViewModel() {


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


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private var initialCryptoList = listOf<CryptoItem>()
    private var isSearchStarting = true

    private val _searchQueryList = MutableStateFlow<List<SearchQuery>>(listOf())
    val searchQueryList: StateFlow<List<SearchQuery>> = _searchQueryList

    private val _state = MutableStateFlow<CoinState>(CoinState())
    val state: StateFlow<CoinState> = _state


    private var job: Job? = null

    fun String.toDoubleOrZero(): Double = this.toDoubleOrNull() ?: 0.0

    private fun selectSymbol(symbol: String) {
        _selectedSymbol.value = symbol
    }

    private fun selectTab(tabIndex: Int) {
        _selectedTabIndex.value = tabIndex
    }

    private fun filterList() {
        val filteredList =
            if (_selectedSymbol.value.isNullOrEmpty() || _selectedSymbol.value == context.getString(
                    R.string.allMarkets
                )
            ) {
                _state.value.coins
            } else {
                _state.value.coins.filter { it.surname == _selectedSymbol.value }
            }



        _tab0.value = filteredList
            .reversed() // Gelen listeyi ters çevir
            .take(10)
        //Log.d("TabFilter", "Tab 0 selected - Reversing list")


        //Log.d("TabFilter", "Tab 1 selected - Sorting by priceChangePercent descending")
        _tab1.value = filteredList
            .sortedByDescending { it.priceChangePercent.replace(",", ".").toDoubleOrNull() ?: 0.0 }
            .take(10)
            .also { sortedList ->
                sortedList.forEach { item ->
                    //Log.d("TabFilter", "Sorted Item symbol: ${item.symbol}, priceChangePercent: ${item.priceChangePercent}")
                }
            }


        //Log.d("TabFilter", "Tab 2 selected - Sorting by priceChangePercent ascending")
        _tab2.value = filteredList
            .sortedBy { it.priceChangePercent.replace(",", ".").toDoubleOrNull() ?: 0.0 }
            .take(10)
            .also { sortedList ->
                sortedList.forEach { item ->
                    //Log.d("TabFilter", "Sorted Item symbol: ${item.symbol}, priceChangePercent: ${item.priceChangePercent}")
                }
            }


        //Log.d("TabFilter", "Tab 3 selected - Sorting by lastPrice descending")
        _tab3.value = filteredList
            .sortedByDescending { it.lastPrice.replace(",", ".").toDoubleOrNull() ?: 0.0 }
            .take(10)


        //Log.d("TabFilter", "Tab 4 selected - Sorting by volume descending")
        _tab4.value = filteredList
            .sortedByDescending { it.volume.replace(",", ".").toDoubleOrZero() }
            .take(10)

    }


    private fun refresh() {
        viewModelScope.launch {
            _state.value = CoinState(isLoading = true)
            try {
                loadCryptos()
                selectTab(_selectedTabIndex.value)
                filterList()
            } catch (e: Exception) {
                _state.value = CoinState(error = e.message ?: "An error occurred")
            }
        }
    }

    private fun loadCryptos() {
        job?.cancel()

        job = getCryptosUseCase.executeGetCryptos().onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = CoinState(coins = it.data ?: emptyList())

                    if (isSearchStarting) {
                        initialCryptoList = it.data!!
                        _state.value = CoinState(coins = initialCryptoList)
                        isSearchStarting = false
                    } else {
                        _state.value = CoinState(coins = applySearchFilter(_state.value.coins))

                    }
                    filterList()
                    _toastMessage.value =
                        "Veriler başarıyla güncellendi." // Başarı durumunda mesajı ayarla

                }

                is Resource.Error -> {
                    _state.value = CoinState(error = it.message ?: "Error!")
                    _toastMessage.value = it.message
                }

                is Resource.Loading -> {
                    _state.value = CoinState(isLoading = true)
                }
            }


        }.launchIn(viewModelScope)

    }

    private fun clearToastMessage() {
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

    private fun searchCryptoList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _searchQuery.value = query
            if (query.isEmpty()) {
                _state.value = CoinState(coins = initialCryptoList)
                isSearchStarting = true
            } else {
                if (isSearchStarting) {
                    initialCryptoList = _state.value.coins.toList()
                    isSearchStarting = false
                }

                val result = initialCryptoList.filter {
                    it.symbol.contains(query.trim(), ignoreCase = true)
                }
                _state.value = CoinState(coins = result)
            }

            // Arama sorgusunu kaydet
        }
    }

    private fun loadSearchQueries() {
        viewModelScope.launch {
            _searchQueryList.value = loadSearchQueriesUseCase()
        }
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            saveSearchQueryUseCase(query)
            loadSearchQueries() // Arama sorgularını güncelle
        }
    }

    private fun deleteSearchQuery(query: SearchQuery) {
        viewModelScope.launch {
            deleteSearchQueryUseCase(query)
            loadSearchQueries() // Arama sorgularını güncelle
        }
    }


    fun onEvent(event: CryptosEvent) {
        when (event) {
            is CryptosEvent.SelectSymbol -> selectSymbol(event.symbol)
            is CryptosEvent.FilterList -> filterList()
            is CryptosEvent.LoadCryptos -> loadCryptos()
            is CryptosEvent.LoadSearchQuery -> loadSearchQueries()
            is CryptosEvent.SelectTab -> selectTab(event.tabIndex)
            is CryptosEvent.SearchCrypto -> searchCryptoList(event.query)
            is CryptosEvent.Refresh -> refresh()
            is CryptosEvent.ClearToastMessage -> clearToastMessage()
            is CryptosEvent.SaveSearchQuery -> saveSearchQuery(event.query)
            is CryptosEvent.DeleteSearchQuery -> deleteSearchQuery(event.query)
        }
    }



}
