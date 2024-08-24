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

    // Event işleme fonksiyonu
    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.GoToPage -> goToPage(event.page)
            MainEvent.NextPage -> {
                if (currentPage.value < pageCount.value - 1) {
                    setCurrentPage(currentPage.value + 1)
                }
            }
            MainEvent.PreviousPage -> {
                if (currentPage.value > 0) {
                    setCurrentPage(currentPage.value - 1)
                }
            }
        }
    }

    private fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    private fun goToPage(page: Int) {
        if (page in 0 until pageCount.value) {
            setCurrentPage(page)
        }
    }
}