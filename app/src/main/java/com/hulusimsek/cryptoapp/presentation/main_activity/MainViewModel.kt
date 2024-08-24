package com.hulusimsek.cryptoapp.presentation.main_activity

import androidx.lifecycle.ViewModel
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {
    private val _pageCount = MutableStateFlow(3) // Sayfa sayısını yönet
    val pageCount: StateFlow<Int> get() = _pageCount

    private val _currentPage = MutableStateFlow(0) // Mevcut sayfayı yönet
    val currentPage: StateFlow<Int> get() = _currentPage

    private val _selectedCoinId = MutableStateFlow<String?>(null)
    val selectedCoinId: StateFlow<String?> get() = _selectedCoinId

    fun selectCoin(coinId: String) {
        _selectedCoinId.value = coinId
        // Sayfa geçişini yap
        goToDetailPage()
    }

    private fun goToDetailPage() {
        _currentPage.value = 1 // Detay sayfasının indexi (örneğin, 1)
    }




    fun setPageCount(count: Int) {
        _pageCount.value = count
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun goToPage(page: Int) {
        if (page in 0 until pageCount.value) {
            setCurrentPage(page)
        }
    }

}
