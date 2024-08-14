package com.hulusimsek.cryptoapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject

@HiltViewModel
class CryptoListViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel() {
    var cryptoList = mutableStateOf<List<CryptoListItem>>(listOf())
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    private var initialCryptoList = listOf<CryptoListItem>()
    private var isSearchStarting = true



    var searchQueryList = mutableStateOf<List<SearchQuery>>(listOf())


    init {
        loadCrpyots()
        loadSearchQueries()
    }

    fun loadCrpyots() {

        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getCryptoList()

            when(result) {
                is Resource.Success -> {
                    val cryptoItems = result.data!!.mapIndexed { index, item ->
                        CryptoListItem(item.symbol,item.price)
                    } as List<CryptoListItem>

                    cryptoList.value += cryptoItems
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


    fun searchCryptoList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            // Eğer arama terimi boşsa, orijinal listeyi geri yükleyelim
            if (query.isEmpty()) {
                cryptoList.value = initialCryptoList
                isSearchStarting = true
                return@launch
            }

            // İlk arama sırasında orijinal listeyi yedekleyelim
            if (isSearchStarting) {
                initialCryptoList = cryptoList.value.toList()
                isSearchStarting = false
                Log.e("CryptoViewModel", "Initial list saved: ${initialCryptoList.size} items")
            }

            // Arama sonuçlarını filtreleyelim
            val result = initialCryptoList.filter {
                it.symbol.contains(query.trim(), ignoreCase = true)
            }
            Log.e("CryptoViewModel", "Search query: $query, Result: ${result.size} items")

            // Filtrelenmiş sonuçları listeye atayalım
            cryptoList.value = result
        }
    }


    fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            repository.insertSearchQuery(SearchQuery(query = query))
            loadSearchQueries() // Reload search queries after saving
        }
    }

    fun deleteSearchQuery(query: SearchQuery) {
        viewModelScope.launch {
            repository.deleteSearchQuery(query)
            loadSearchQueries() // Silme işleminden sonra geçmiş sorguları yeniden yükleyin.
        }
    }

    // Function to load search queries from the repository
    private fun loadSearchQueries() {
        viewModelScope.launch {
            searchQueryList.value = repository.getSearchQuery()
        }
    }



}