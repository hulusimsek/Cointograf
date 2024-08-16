package com.hulusimsek.cryptoapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel() {
    val pageCount = mutableStateOf(0) // Sayfa sayısını yönet
    val currentPage = mutableStateOf(0) // Mevcut sayfayı yönet

    fun setPageCount(count: Int) {
        pageCount.value = count
    }

    fun setCurrentPage(page: Int) {
        currentPage.value = page
    }

    fun goToPage(page: Int) {
        if (page in 0 until pageCount.value) {
            setCurrentPage(page)
        }
    }

}
