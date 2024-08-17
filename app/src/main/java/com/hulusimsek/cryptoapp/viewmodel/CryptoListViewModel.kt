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

    private val _cryptoList = MutableStateFlow<List<CryptoListItem>>(listOf())
    val cryptoList: StateFlow<List<CryptoListItem>> = _cryptoList

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var initialCryptoList = listOf<CryptoListItem>()
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
            println("güncellendi")
            _isLoading.value = false
        }
    }

    fun loadCrpyots() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCryptoList()

            when (result) {
                is Resource.Success -> {
                    _cryptoList.value = listOf()
                    val cryptoItems = result.data!!.mapIndexed { index, item ->
                        CryptoListItem(item.symbol, item.price)
                    }

                    _cryptoList.value = cryptoItems
                    _errorMessage.value = ""
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

    fun searchCryptoList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                _cryptoList.value = initialCryptoList
                isSearchStarting = true
                return@launch
            }

            if (isSearchStarting) {
                initialCryptoList = _cryptoList.value.toList()
                isSearchStarting = false
                Log.e("CryptoViewModel", "Initial list saved: ${initialCryptoList.size} items")
            }

            val result = initialCryptoList.filter {
                it.symbol.contains(query.trim(), ignoreCase = true)
            }
            Log.e("CryptoViewModel", "Search query: $query, Result: ${result.size} items")

            _cryptoList.value = result
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

    private fun loadSearchQueries() {
        viewModelScope.launch {
            _searchQueryList.value = repository.getSearchQuery()
        }
    }
}